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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

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

    /**
     * Clears all groups this entry is assigned to.
     * This will not affect this object, because it's saved in the groups what members they have.
     *
     * @return how many groups has been affected by the changes.
     */
    int wipeGroups();

    /**
     * This method returns all groups of this specific permission entry
     *
     * @return A set of all groups
     */
    @NotNull @Unmodifiable Set<PermissionGroup<T>> getPermissionGroups();

    /**
     * This method checks if the permission entry has a specific group
     *
     * @param permissionGroup The group to check
     * @return true if the group is added, false if not
     */
    boolean inGroup(@NotNull PermissionGroup<T> permissionGroup);
}
