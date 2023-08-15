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

package net.kissenpvp.core.api.permission;

import net.kissenpvp.core.api.event.EventCancelledException;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;


public interface PermissionEntry<T extends Permission> {

    /**
     * This method returns the unique id of the permission entry.
     *
     * @return The unique id of the player.
     */
    @NotNull String getPermissionID();

    @NotNull Component displayName();

    @NotNull T setPermission(@NotNull String permission) throws EventCancelledException;

    @NotNull T setPermission(@NotNull String permission, boolean value) throws EventCancelledException;

    @NotNull T setPermission(@NotNull T permission) throws EventCancelledException;

    boolean unsetPermission(@NotNull String permission);

    int wipePermissions();

    @NotNull @Unmodifiable Set<T> getPermissionList();

    @NotNull Optional<T> getPermission(@NotNull String permission);

    @NotNull @Unmodifiable Set<UUID> getAffectedPermissionPlayer();

    boolean hasPermission(@NotNull String permission);

    void permissionUpdate();
}
