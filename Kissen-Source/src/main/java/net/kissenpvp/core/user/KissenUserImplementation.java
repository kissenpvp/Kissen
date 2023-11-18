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
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.ObjectMeta;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.FilterType;
import net.kissenpvp.core.api.database.queryapi.select.QuerySelect;
import net.kissenpvp.core.api.database.queryapi.update.QueryUpdateDirective;
import net.kissenpvp.core.api.database.savable.SavableMap;
import net.kissenpvp.core.api.networking.client.entitiy.UnknownPlayerException;
import net.kissenpvp.core.api.permission.Permission;
import net.kissenpvp.core.api.task.TaskException;
import net.kissenpvp.core.api.task.TaskImplementation;
import net.kissenpvp.core.api.user.User;
import net.kissenpvp.core.api.user.UserImplementation;
import net.kissenpvp.core.api.user.UserInfo;
import net.kissenpvp.core.api.user.usersetttings.PlayerSetting;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.message.usersettings.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
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
public abstract class KissenUserImplementation implements UserImplementation {

    @Getter
    private final Set<User> onlineUserSet;
    @Getter(AccessLevel.PROTECTED)
    private final Set<UserInfoNode> cachedProfiles;
    private final Set<PlayerSetting<?>> userPlayerSettings;

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
        this.onlineUserSet = new HashSet<>();
        this.cachedProfiles = new HashSet<>();
        this.userPlayerSettings = new HashSet<>();
    }

    @Override
    public boolean postStart() {
        try {
            TaskImplementation taskImplementation = KissenCore.getInstance().getImplementation(TaskImplementation.class);
            Runnable runnable = () -> getOnlineUser().stream().filter(userEntry -> userEntry.getStorage().containsKey("tick")).forEach(user -> ((KissenUser<? extends Permission>) user).tick());
            taskImplementation.registerAsyncTask("user_tick", 20, runnable);

            registerUserSetting(new PrimaryUserColor());
            registerUserSetting(new SecondaryUserColor());
            registerUserSetting(new GeneralUserColor());
            registerUserSetting(new EnabledUserColor());
            registerUserSetting(new DisabledUserColor());

        } catch (TaskException taskException) {
            throw new IllegalStateException("Cannot start user tick!", taskException);
        }

        cachedProfiles.clear();
        cachedProfiles.addAll(fetchUserProfiles());

        return UserImplementation.super.postStart();
    }

    @Override
    public @NotNull @Unmodifiable Set<User> getOnlineUser() {
        return Collections.unmodifiableSet(onlineUserSet);
    }

    @Override
    public @NotNull Optional<User> getOnlineUser(@NotNull UUID uuid) {
        return onlineUserSet.stream().filter(userEntry -> userEntry.getRawID().equals(uuid.toString())).findFirst();
    }

    @Override
    public @NotNull User getUser(@NotNull String name) throws BackendException {
        return getOnlineUser().stream().filter(user -> user.getNotNull("name").equals(name)).findFirst().orElseGet(() ->
        {
            try {
                String[][] data = getUserMeta().select(Column.TOTAL_ID).where(Column.KEY, "name", FilterType.EQUALS).and(Column.VALUE, name, FilterType.EQUALS).execute();
                if (data.length != 0) {
                    return getUser(UUID.fromString(data[0][0].substring(getUserSaveID().length())));
                }
                throw new UnknownPlayerException(name);
            } catch (BackendException backendException) {
                throw new RuntimeException(backendException);
            }
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

    @Override public @Unmodifiable @NotNull Set<UserInfo> getCachedUserProfiles()
    {
        return getCachedProfiles().stream().map(UserInfoNode::getUserInfo).collect(Collectors.toSet());
    }

    @Override
    public @Unmodifiable @NotNull <T> Set<PlayerSetting<?>> registerUserSetting(@NotNull PlayerSetting<T> playerSetting) {
        Set<PlayerSetting<?>> playerSettings = userPlayerSettings.stream()
                .filter(playerSetting1 -> playerSetting1.getKey().equals(playerSetting.getKey()))
                .collect(Collectors.toUnmodifiableSet());
        userPlayerSettings.removeAll(playerSettings);
        userPlayerSettings.add(playerSetting);
        return playerSettings;
    }

    @Override
    public @NotNull @Unmodifiable Set<PlayerSetting<?>> getUserSettings() {
        return Collections.unmodifiableSet(userPlayerSettings);
    }

    /**
     * Retrieves a user's data.
     * <p>
     * This method is responsible for fetching and returning a user's data. The data is stored in a collection of SavableMap objects,
     * where each SavableMap represents a set of user's data that can be stored and retrieved.
     * <p>
     * By marking this collection with the @Unmodifiable annotation, it signifies that the returned collection is unmodifiable,
     * meaning that any attempt to alter its state (add, remove, etc.) will throw an UnsupportedOperationException.
     *
     * @return A set of SavableMap objects representing the user's data. The returned set is unmodifiable.
     * @throws BackendException If an error occurs while trying to fetch the user's data from the backend data source.
     */
    public @NotNull @Unmodifiable abstract Set<SavableMap> getUserData() throws BackendException;

    /**
     * Provides a default user save ID.
     * <p>
     * This method returns a constant string denoting a generic user save ID. Currently, it returns the string "user".
     * The purpose of this method is to provide a simple, uniform identifier that can be used for saving and retrieving user information.
     *
     * @return String The constant user save ID. In this case, it is "user".
     */
    public @NotNull ObjectMeta getUserMeta() {
        return KissenCore.getInstance().getPublicMeta();
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
    private @Unmodifiable @NotNull Set<UserInfoNode> fetchUserProfiles() {
        Set<UserInfoNode> userInfos = new HashSet<>();
        QuerySelect querySelect = getUserMeta().select(Column.TOTAL_ID, Column.VALUE).where(Column.TOTAL_ID, getUserSaveID(), FilterType.START).and(Column.KEY, "name", FilterType.EQUALS);
        try {
            String[][] data = querySelect.execute();
            for (String[] user : data) {
                UUID uuid = UUID.fromString(user[0].substring(getUserSaveID().length()));
                String name = user[1];

                userInfos.add(new UserInfoNode(uuid, name));
            }
        } catch (BackendException backendException) {
            throw new IllegalStateException("The userinfo couldn't be cached. Something must be wrong with the sql connection. The system shuts down to prevent further damages to the systems database.", backendException);
        }
        KissenCore.getInstance().getLogger().info("Successfully loaded {} user profile(s) from the database.", userInfos.size());
        return Collections.unmodifiableSet(userInfos);
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
        Optional<User> userOptional = getOnlineUser(UUID.fromString(user.getRawID()));
        if (userOptional.isPresent()) {
            if (userOptional.get().getStorage().containsKey("tick")) {
                return false;
            }
            getOnlineUserSet().removeIf(currentUser -> currentUser.getRawID().equals(user.getRawID()));
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
    public boolean loginUser(User user) {
        if (onlineUserSet.contains(user)) {
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
        if (getOnlineUser(UUID.fromString(user.getRawID())).isPresent()) {
            ((KissenUser<?>) user).logout();
            return onlineUserSet.removeIf(userEntry -> userEntry.getRawID().equals(user.getRawID()));
        }
        return false;
    }

    /**
     * Update a user's TOTAL_ID value.
     * <p>
     * This method allows you to change the TOTAL_ID of a user. It creates and executes an update command,
     * where the TARGET_ID is replaced with a new one in the metadata.
     *
     * @param from a UUID representing the original TOTAL_ID (TARGET_ID) of the user to change
     * @param to   a UUID representing the new TOTAL_ID of the user
     * @return the number of rows affected by the update
     * @throws Exception if an error occurs during the execution of the update
     */
    public long rewriteTotalID(@NotNull UUID from, @NotNull UUID to) {
        return getUserMeta().update(new QueryUpdateDirective(Column.VALUE, to.toString()))
                .where(Column.TOTAL_ID, getUserSaveID(), FilterType.START)
                .and(Column.KEY, "total_id", FilterType.EQUALS)
                .and(Column.VALUE, from.toString(), FilterType.EQUALS).execute();

    }

    private @NotNull Optional<UserInfo> getUserInfo(@NotNull Predicate<UserInfoNode> name) {
        return getCachedProfiles().stream().filter(name).map(UserInfoNode::getUserInfo).findFirst();
    }

    public void cacheProfile(@NotNull UserInfoNode userInfoNode)
    {
        if(getCachedProfiles().stream().anyMatch(current -> current.uuid().equals(userInfoNode.uuid()) && current.name().equals(userInfoNode.name())))
        {
            return;
        }
        getCachedProfiles().remove(userInfoNode);
        getCachedProfiles().add(userInfoNode);
        KissenCore.getInstance().getLogger().info("The profile {} has been cached now.", userInfoNode);
    }
}
