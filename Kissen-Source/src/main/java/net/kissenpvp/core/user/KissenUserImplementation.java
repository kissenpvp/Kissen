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

import lombok.Getter;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.ObjectMeta;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.FilterType;
import net.kissenpvp.core.api.database.queryapi.QuerySelect;
import net.kissenpvp.core.api.database.queryapi.QueryUpdateDirective;
import net.kissenpvp.core.api.database.savable.SavableMap;
import net.kissenpvp.core.api.networking.client.entitiy.UnknownPlayerException;
import net.kissenpvp.core.api.permission.Permission;
import net.kissenpvp.core.api.task.TaskException;
import net.kissenpvp.core.api.task.TaskImplementation;
import net.kissenpvp.core.api.user.User;
import net.kissenpvp.core.api.user.UserImplementation;
import net.kissenpvp.core.api.user.usersetttings.PlayerSetting;
import net.kissenpvp.core.base.KissenCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;


public abstract class KissenUserImplementation implements UserImplementation {

    @Getter
    private final Set<User> onlineUserSet;
    private final Set<String> cachedNames;
    private final Set<PlayerSetting<?>> userPlayerSettings;

    public KissenUserImplementation() {
        onlineUserSet = new HashSet<>();
        this.cachedNames = new HashSet<>();
        this.userPlayerSettings = new HashSet<>();
    }

    @Override
    public boolean postStart() {
        try {
            KissenCore.getInstance()
                    .getImplementation(TaskImplementation.class)
                    .registerAsyncTask("user_tick", 20, () -> getOnlineUser().stream()
                            .filter(userEntry -> userEntry
                                    .getStorage()
                                    .containsKey("tick"))
                            .forEach(user -> ((KissenUser<? extends Permission>) user).tick()));
        } catch (TaskException taskException) {
            throw new IllegalStateException("Cannot start user tick!", taskException);
        }

        cachedNames.clear();

        QuerySelect querySelect = getUserMeta()
                .select(Column.VALUE)
                .appendFilter(Column.TOTAL_ID, getUserSaveID(), FilterType.START)
                .appendFilter(Column.KEY, "name", FilterType.EQUALS);
        try {
            cachedNames.addAll(Arrays.stream(getUserMeta().execute(querySelect))
                    .flatMap(Arrays::stream)
                    .toList());
        } catch (BackendException backendException) {
            throw new IllegalStateException("The names couldn't be cached. Something must be wrong with the sql connection. The system shuts down to prevent further damages to the systems database.", backendException);
        }

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
                String[][] data = getUserMeta().execute(getUserMeta().select(Column.TOTAL_ID).appendFilter(Column.KEY, "name", FilterType.EQUALS).appendFilter(Column.VALUE, name, FilterType.EQUALS));
                if(data.length != 0)
                {
                    return getUser(UUID.fromString(data[0][0].substring(getUserSaveID().length())));
                }
                throw new UnknownPlayerException(name);
            } catch (BackendException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public boolean isValid(@NotNull UUID uuid) throws BackendException {
        return getUserMeta().metaContains(getUserSaveID() + uuid, "id");
    }

    @Override
    public boolean isValid(@NotNull String name) throws BackendException {
        if (cachedNames.contains(name)) {
            return true;
        }

        return getUserMeta().execute(getUserMeta().select(Column.VALUE)
                .appendFilter(Column.KEY, "name", FilterType.EQUALS)
                .appendFilter(Column.VALUE, name, FilterType.EQUALS)).length != 0;
    }

    public @NotNull
    @Unmodifiable
    abstract Set<SavableMap> getUserData() throws BackendException;

    @Override
    public @NotNull Set<String> getUserNames() {
        return Collections.unmodifiableSet(cachedNames);
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

    public @NotNull ObjectMeta getUserMeta() {
        return KissenCore.getInstance().getPublicMeta();
    }

    public @NotNull String getUserSaveID() {
        return "user";
    }

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

    public boolean loginUser(User user) {
        if (onlineUserSet.contains(user)) {
            ((KissenUser<?>) user).login();
            return true;
        }
        return false;
    }

    public boolean logoutUser(@NotNull User user) {
        if (getOnlineUser(UUID.fromString(user.getRawID())).isPresent()) {
            ((KissenUser<?>) user).logout();
            return onlineUserSet.removeIf(userEntry -> userEntry.getRawID().equals(user.getRawID()));
        }
        return false;
    }

    public Set<String> getCachedNames() {
        return cachedNames;
    }

    public long rewriteTotalID(@NotNull UUID from, @NotNull UUID to) throws Exception {
        return getUserMeta().execute(getUserMeta().update(new QueryUpdateDirective(Column.VALUE, to.toString()))
                .appendFilter(Column.TOTAL_ID, getUserSaveID(), FilterType.START)
                .appendFilter(Column.KEY, "total_id", FilterType.EQUALS)
                .appendFilter(Column.VALUE, from.toString(), FilterType.EQUALS));

    }
}
