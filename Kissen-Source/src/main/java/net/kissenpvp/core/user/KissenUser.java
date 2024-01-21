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

import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.ObjectMeta;
import net.kissenpvp.core.api.database.savable.SavableInitializeException;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.permission.GroupablePermissionEntry;
import net.kissenpvp.core.api.permission.Permission;
import net.kissenpvp.core.api.user.User;
import net.kissenpvp.core.api.user.UserImplementation;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.savable.SerializableSavableHandler;
import net.kissenpvp.core.permission.KissenGroupablePermissionEntry;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;


public abstract class KissenUser<T extends Permission> extends KissenGroupablePermissionEntry<T> implements User {
    public KissenUser(@Nullable UUID uuid, @Nullable String name) throws BackendException {
        this(uuid, name, null);
    }

    public KissenUser(@Nullable UUID uuid, @Nullable String name, @Nullable Map<String, String> data) throws BackendException {
        if (uuid != null) {
            try {
                setup(uuid.toString(), data);
            } catch (SavableInitializeException savableInitializeException) {
                setup(uuid.toString(), getDefaultData(uuid, name));
            }
        }
    }

    @Override
    public @NotNull @Unmodifiable Set<UUID> getAffectedUsers() {
        return Collections.singleton(UUID.fromString(getRawID()));
    }

    @Override
    public @NotNull @Unmodifiable Set<GroupablePermissionEntry<T>> getConnectedEntries()
    {
        return Collections.singleton(this);
    }

    @Override
    public void permissionUpdate()
    {
        clearCache();
        ((GroupablePermissionEntry<T>) getPlayerClient()).permissionUpdate();
    }

    public void clearCache()
    {
        super.permissionUpdate();
    }

    protected @NotNull @Unmodifiable Map<String, String> getDefaultData(UUID uuid, String name) {
        return Collections.unmodifiableMap(new HashMap<>());
    }

    @Override
    public @NotNull Map<String, Object> getStorage()
    {
        UserImplementation userImplementation = KissenCore.getInstance().getImplementation(UserImplementation.class);
        if(userImplementation.getOnlineUser(UUID.fromString(getRawID())).isEmpty())
        {
            return new HashMap<>(); // not connected
        }
        return super.getStorage();
    }

    @Override
    public SerializableSavableHandler getSerializableSavableHandler() {
        return new SerializableUserHandler(UUID.fromString(getRawID()));
    }

    @Override
    public @NotNull Component displayName() {
        return getPlayerClient().displayName();
    }

    /**
     * Called when the user logs in.
     */
    public void login() {
    }

    /**
     * Called when the user logs out.
     */
    public void logout() {
    }

    /**
     * This method is called every 20 ticks.
     */
    public void tick() {
    }

    /**
     * Writes the online expire the in the database.
     * Most likely this is called when the user quits.
     */
    protected void writeOnlineTimeData() {
        if (getStorage().containsKey("time_joined")) {
            getStorage().put("block_networking_update", 0);
            set("last_played", String.valueOf(System.currentTimeMillis()));

            long onlineTime = 0;
            if (containsKey("online_time")) {
                onlineTime = Long.parseLong(getNotNull("online_time"));
            }

            set("online_time", String.valueOf(onlineTime + (System.currentTimeMillis() - ((long) getStorage().get("time_joined")))));
            getStorage().put("time_joined", System.currentTimeMillis());
            getStorage().remove("block_networking_update", 0);
        }
    }

    @Override
    public @NotNull ObjectMeta getMeta() {
        return KissenCore.getInstance().getImplementation(KissenUserImplementation.class).getUserMeta();
    }
}
