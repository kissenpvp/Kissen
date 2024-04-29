/*
 * Copyright (C) 2023 KissenPvP
 *
 * This program is licensed under the Apache License, Version 2.0.
 *
 * This software may be redistributed and/or modified under the terms
 * of the Apache License as published by the Apache Software Foundation,
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the Apache
 * License, Version 2.0 for the specific language governing permissions
 * and limitations under the License.
 *
 * You should have received a copy of the Apache License, Version 2.0
 * along with this program. If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package net.kissenpvp.core.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import net.kissenpvp.core.api.database.connection.DatabaseImplementation;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.Meta;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.select.QuerySelect;
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.permission.AbstractPermission;
import net.kissenpvp.core.api.user.User;
import net.kissenpvp.core.api.user.UserImplementation;
import net.kissenpvp.core.api.user.UserInfo;
import net.kissenpvp.core.api.user.playersettting.AbstractPlayerSetting;
import net.kissenpvp.core.api.user.playersettting.RegisteredPlayerSetting;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.command.confirmation.KissenConfirmationImplementation;
import net.kissenpvp.core.database.KissenTable;
import net.kissenpvp.core.permission.InternalPermissionImplementation;
import net.kissenpvp.core.user.playersetting.KissenRegisteredPlayerSetting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Abstract implementation of the UserImplementation interface.
 * <p>
 * The KissenUserImplementation serves as a scaffold for other classes that implement the UserImplementation interface. It provides a structure that the child classes can follow.
 * <p>
 * This class is abstract as it is not intended to be instantiated on its own. Instead, developers should extend it and provide concrete implementations to any abstract methods declared in this class or in the UserImplementation interface.
 * <p>
 * In the implementation of the UserImplementation interface, this class provides some basic concrete functionality and leaves the rest to its subclasses. It is a way of enforcing a certain structure on the user-related classes in the system.
 */
@Slf4j
public abstract class KissenUserImplementation implements UserImplementation {

    @Getter private final static Set<User> onlineUserSet;

    static {
        onlineUserSet = new HashSet<>(); // persist when reloading
    }

    @Getter(AccessLevel.PROTECTED) private final Set<UserInfoNode> cachedProfiles;
    private final Set<KissenRegisteredPlayerSetting<?, ?>> pluginSettings;
    private final ScheduledExecutorService tickExecutor;
    @Getter private KissenTable Table;

    /**
     * Initializes the KissenUserImplementation instance.
     * <p>
     * This is a no-arguments constructor that constructs a new KissenUserImplementation object. When a new KissenUserImplementation object is created,
     * it initializes the 'onlineUserSet', 'cachedProfiles', and 'userPlayerSettings' as new instances of a HashSet.
     * <p>
     * The 'onlineUserSet' set is used to keep track of all users who are currently online.
     * The 'cachedProfiles' set maintains a cache of user profiles, likely to avoid repetitive and costly database operations.
     * The 'userPlayerSettings' is meant to hold the settings data of the player.
     */
    public KissenUserImplementation() {
        this.cachedProfiles = new HashSet<>();
        this.pluginSettings = new HashSet<>();
        this.tickExecutor = Executors.newScheduledThreadPool(1);
    }

    @Override
    public boolean preStart() {
        DatabaseImplementation database = KissenCore.getInstance().getImplementation(DatabaseImplementation.class);
        Table = (KissenTable) database.getPrimaryConnection().createTable("kissen_user_data");
        return UserImplementation.super.preStart();
    }

    @Override
    public boolean postStart() {

        cachedProfiles.clear();
        cachedProfiles.addAll(fetchUserProfiles());
        KissenCore.getInstance().getLogger().info("Successfully loaded {} user profile(s) from the database.", cachedProfiles.size());

        Class<KissenConfirmationImplementation> clazz = KissenConfirmationImplementation.class;
        KissenConfirmationImplementation confirmation = KissenCore.getInstance().getImplementation(clazz);

        Runnable runnable = () -> {
            getOnlineUser().stream().filter(userEntry -> userEntry.getStorage().containsKey("tick")).forEach(user -> {
                KissenUser<? extends AbstractPermission> casted = (KissenUser<? extends AbstractPermission>) user;
                try {
                    casted.tick();
                } catch (Exception exception) {
                    log.error("An exception was caught executing the tick of user {}.", user.getRawID(), exception);
                }
            });
            confirmation.cleanUp();
        };

        this.tickExecutor.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);
        return UserImplementation.super.postStart();
    }

    @Override
    public void stop() {
        this.tickExecutor.shutdown();
    }

    @Override
    public @NotNull @Unmodifiable Set<User> getOnlineUser() {
        return Collections.unmodifiableSet(onlineUserSet);
    }

    @Override
    public @NotNull Optional<User> getOnlineUser(@NotNull UUID uuid) {
        return getOnlineUser().stream().filter(userEntry -> Objects.equals(userEntry.getRawID(), uuid)).findFirst();
    }

    @Override
    public @NotNull User getUser(@NotNull String name) throws BackendException {
        return getOnlineUser().stream().filter(user -> Objects.equals(user.getPlayerClient().getName(), name)).findFirst().orElseGet(() -> {
            //TODO make this work someday
            throw new BackendException();
        });
    }

    @Override
    public boolean isValid(@NotNull UUID uuid) {
        return getCachedProfiles().stream().anyMatch(userInfo -> userInfo.uuid().equals(uuid));
    }

    @Override
    public boolean isValid(@NotNull String name) {
        return getCachedProfiles().stream().anyMatch(userInfo -> userInfo.name().equals(name));
    }

    @Override
    public @NotNull Set<UserInfo> getUserProfiles() {
        return getCachedProfiles().stream().map(UserInfoNode::getUserInfo).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @Unmodifiable @NotNull Optional<UserInfo> getCachedUserProfile(@NotNull String name) {
        return getUserInfo((userInfoNode -> userInfoNode.name().equals(name)));
    }

    @Override
    public @Unmodifiable @NotNull Optional<UserInfo> getCachedUserProfile(@NotNull UUID uuid) {
        return getUserInfo(userInfoNode -> userInfoNode.uuid().equals(uuid));
    }

    @Override
    public @Unmodifiable @NotNull Set<UserInfo> getCachedUserProfiles() {
        return getCachedProfiles().stream().map(UserInfoNode::getUserInfo).collect(Collectors.toSet());
    }

    @Override
    public <T> void registerPlayerSetting(@NotNull KissenPlugin kissenPlugin, @NotNull AbstractPlayerSetting<T, ?> playerSetting) throws EventCancelledException {

        if (isKeyTaken(playerSetting)) {
            String errorMessage = String.format("Settings key %s is already registered.", playerSetting.getKey());
            throw new EventCancelledException(new IllegalArgumentException(errorMessage));
        }

        KissenRegisteredPlayerSetting<?, ?> setting = new KissenRegisteredPlayerSetting<>(kissenPlugin, playerSetting);
        if (pluginSettings.add(setting)) {
            setting.getPermission().ifPresent(permission -> {
                InternalPermissionImplementation<?> permissionImplementation = KissenCore.getInstance().getImplementation(InternalPermissionImplementation.class);
                permissionImplementation.addPermission(permission);
            });
            return;
        }
        throw new EventCancelledException();
    }

    @Override
    public <T> @NotNull RegisteredPlayerSetting<T, ?> getPlayerSetting(@NotNull Class<? extends AbstractPlayerSetting<T, ?>> clazz) {
        return (RegisteredPlayerSetting<T, ?>) getPlayerSettings().stream().filter(setting -> clazz.isAssignableFrom(setting.getParentClass())).findFirst().orElseThrow();
    }

    @Override
    public @NotNull @Unmodifiable Set<RegisteredPlayerSetting<?, ?>> getPlayerSettings() {
        Predicate<KissenRegisteredPlayerSetting<?, ?>> isEnabled = setting -> setting.plugin().isEnabled();
        return pluginSettings.stream().filter(isEnabled).collect(Collectors.toUnmodifiableSet());
    }

    private boolean isKeyTaken(@NotNull AbstractPlayerSetting<?, ?> setting) {
        return getPlayerSettings().stream().anyMatch(currentSetting -> {
            String parentKey = currentSetting.getParent().getKey();
            return Objects.equals(parentKey, setting.getKey());
        });
    }

    /**
     * Fetches and caches user profiles.
     * <p>
     * This method fetches the user profiles by executing a Select query against the MetaData of the User.
     * It then extracts the UUID and name for each user from the resulting dataset and encapsulates them into a UserInfo object.
     * These UserInfo objects are then stored in the cachedProfiles.
     * <p>
     * The method retrieves the TOTAL_ID and VALUE columns, filters the results where TOTAL_ID starts with the User's Save ID
     * and the 'name' key is equal to the 'name' specified in the filter.
     * <p>
     * If the BackendException is thrown while data fetching, it is captured and transformed into the IllegalStateException
     * and the system will shut down to prevent further damages to the system's database.
     *
     * @throws IllegalStateException if an error occurs while fetching the user profiles from the backend data source
     */
    private @NotNull @Unmodifiable Set<UserInfoNode> fetchUserProfiles() {
        Set<UserInfoNode> userInfos = new HashSet<>();
        QuerySelect querySelect = getMeta().select(Column.TOTAL_ID, Column.VALUE).where(Column.TOTAL_ID, "^" + getUserSaveID()).and(Column.KEY, "name");
        Object[][] data = querySelect.execute().join();
        for (Object[] user : data) {
            UUID uuid = UUID.fromString(user[0].toString().substring(getUserSaveID().length()));
            String name = user[1].toString();
            userInfos.add(new UserInfoNode(uuid, name));
        }
        return userInfos;
    }

    public @NotNull Meta getMeta() {
        return getTable().setupMeta(null);
    }

    /**
     * Provides a default user save ID.
     * <p>
     * This method returns a constant string denoting a generic user save ID. Currently, it returns the string "user".
     * The purpose of this method is to provide a simple, uniform identifier that can be used for saving and retrieving user information.
     *
     * @return String The constant user save ID. In this case, it is "user".
     */
    public @NotNull String getUserSaveID() {
        return "user";
    }

    /**
     * Loads a given user into the online user set.
     * <p>
     * The method starts by converting the raw ID of the provided User object into a UUID and then searches for the user with this UUID in the current online users. This is done using the getOnlineUser() function.
     * <p>
     * If the user is already present online, the method checks if the user's storage contains the 'tick' attribute. In such a case, the user is considered as already loaded and the method returns false immediately, indicating that it failed to load the user anew.
     * <p>
     * If the user's storage doesn't contain the 'tick' attribute, the method proceeds to remove the present user from onlineUserSet. This is done by invoking the removeIf method on the 'getOnlineUserSet', passing in a lambda that checks user raw id equality.
     * <p>
     * After the previous user instance has been removed (if it was present and not already loaded), the provided User object is added to the onlineUserSet collection.
     * <p>
     * In the end, the method returns true, indicating that the user was successfully loaded into the online users set.
     *
     * @param user the User object that is to be loaded into the online users; not null
     * @return boolean indicating whether user data was successfully loaded into online users (true if loaded, otherwise false)
     */
    public boolean loadUser(@NotNull User user) {
        Optional<User> userOptional = getOnlineUser((UUID) user.getRawID());
        if (userOptional.isPresent()) {
            onlineUserSet.removeIf(currentUser -> currentUser.getRawID().equals(user.getRawID()));
        }
        onlineUserSet.add(user);
        return true;
    }

    /**
     * Login a user if the user is contained in onlineUserSet.
     *
     * @param user a User entity representing the user to login
     * @return true if the user was successfully logged in, false otherwise
     */
    public boolean loginUser(@NotNull User user) {
        if (getOnlineUser().contains(user)) {
            ((KissenUser<?>) user).login();
            return true;
        }
        return false;
    }

    /**
     * Logout a user if the user is currently online.
     *
     * @param user a User entity representing the user to logout
     * @return true if the user was successfully logged out, false otherwise
     */
    public boolean logoutUser(@NotNull User user) {
        if (getOnlineUser((UUID) user.getRawID()).isPresent()) {
            ((KissenUser<?>) user).logout();
            return onlineUserSet.removeIf(userEntry -> userEntry.getRawID().equals(user.getRawID()));
        }
        return false;
    }

    public @NotNull CompletableFuture<Long> rewriteTotalID(@NotNull UUID from, @NotNull UUID to) {
        //return getUserMeta().update(new Update(Column.VALUE, to.toString())).where(Column.TOTAL_ID, getUserSaveID()).and(Column.KEY, "total_id", FilterType.EXACT_MATCH).and(Column.VALUE, from.toString(), FilterType.EXACT_MATCH).execute();
        return CompletableFuture.completedFuture(0L); //TODO
    }

    private @NotNull Optional<UserInfo> getUserInfo(@NotNull Predicate<UserInfoNode> name) {
        return getCachedProfiles().stream().filter(name).map(UserInfoNode::getUserInfo).findFirst();
    }

    public void cacheProfile(@NotNull UserInfoNode userInfoNode) {
        if (getCachedProfiles().stream().anyMatch(current -> current.uuid().equals(userInfoNode.uuid()) && current.name().equals(userInfoNode.name()))) {
            return;
        }
        getCachedProfiles().remove(userInfoNode);
        getCachedProfiles().add(userInfoNode);
        KissenCore.getInstance().getLogger().info("The profile {} has been cached.", userInfoNode);
    }
}
