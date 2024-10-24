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

package net.kissenpvp.core.api.permission;

import net.kissenpvp.core.api.event.EventCancelledException;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;


public interface AbstractPermissionEntry<T extends AbstractPermission> {

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

    @NotNull @Unmodifiable Set<UUID> getAffectedUsers();

    @NotNull @Unmodifiable Set<AbstractGroupablePermissionEntry<T>> getConnectedEntries();

    boolean hasPermission(@NotNull String permission);

    void permissionUpdate();
}
