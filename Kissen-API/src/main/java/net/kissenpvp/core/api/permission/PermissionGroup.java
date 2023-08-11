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

/**
 * A permission group is a group containing permissions and assigns them to the {@link PermissionEntry} which are members of it.
 *
 * @author Groldi
 * @since 1.0.0-SNAPSHOT
 */
public interface PermissionGroup<T extends Permission> extends GroupablePermissionEntry<T>
{
    /**
     * This method returns all members of this group.
     *
     * @return all members of this group
     */
    @NotNull @Unmodifiable Set<String> getMember();

    /**
     * This method add a member to this permission group.
     * If the member is already a member of this group, nothing will happen.
     *
     * @param groupablePermissionEntry the member to add
     * @return true if the member was added, false if the member was already a member of this group
     */
    boolean addMember(@NotNull GroupablePermissionEntry<?> groupablePermissionEntry) throws PermissionGroupConflictException;

    /**
     * This method removes a member from this permission group.
     *
     * @param groupablePermissionEntry the member to remove
     * @return true if the member was removed, false if the member was not a member of this group
     */
    boolean removeMember(@NotNull GroupablePermissionEntry<?> groupablePermissionEntry);
}
