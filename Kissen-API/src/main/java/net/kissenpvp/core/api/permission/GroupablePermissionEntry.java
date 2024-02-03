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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GroupablePermissionEntry<T extends Permission> extends PermissionEntry<T> {

    /**
     * Returns permission that this entry has as its own.
     * Because this object can be a member of a group, not all permissions that this object are its own.
     * This method filters all permissions out, that come from 3'rd party groups
     *
     * @return a set which contains all permissions from this specific object.
     */
    @NotNull @Unmodifiable Set<T> getOwnPermissions();

    @NotNull Optional<T> getOwnPermission(@NotNull String permission);

    /**
     * Clears all groups this entry is assigned to.
     * This will not affect this object, because it's saved in the groups what members they have.
     *
     * @return how many groups has been affected by the changes.
     */
    int wipeGroups();

    @NotNull @Unmodifiable List<PermissionGroup<T>> getOwnPermissionGroups();

    /**
     * This method returns all groups of this specific permission entry
     *
     * @return A set of all groups
     */
    @NotNull @Unmodifiable List<PermissionGroup<T>> getPermissionGroups();

    /**
     * This method checks if the permission entry has a specific group
     *
     * @param permissionGroup The group to check
     * @return true if the group is added, false if not
     */
    boolean inGroup(@NotNull PermissionGroup<T> permissionGroup);
}
