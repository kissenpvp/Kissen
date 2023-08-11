/*
 * Copyright 2023 KissenPvP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.kissenpvp.core.user;

import net.kissenpvp.core.api.database.StorageImplementation;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.savable.SavableInitializeException;
import net.kissenpvp.core.api.permission.Permission;
import net.kissenpvp.core.api.user.User;
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
    public @NotNull @Unmodifiable Set<UUID> getAffectedPermissionPlayer() {
        return Collections.singleton(UUID.fromString(getRawID()));
    }

    protected @NotNull @Unmodifiable Map<String, String> getDefaultData(UUID uuid, String name) {
        return Collections.unmodifiableMap(new HashMap<>());
    }

    @Override
    public @NotNull Map<String, Object> getStorage() {
        return KissenCore.getInstance().getImplementation(StorageImplementation.class).getStorage("user" + getRawID());
    }

    @Override
    public SerializableSavableHandler getSerializableSavableHandler() {
        return new SerializableUserHandler(UUID.fromString(getRawID()));
    }

    @Override
    public @NotNull Component displayName() {
        return getPlayerClient().displayName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof KissenUser<?> userEntry)) {
            return false;
        }

        return userEntry.getId().equals(getId());
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
     * Writes the online time the in the database.
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
}