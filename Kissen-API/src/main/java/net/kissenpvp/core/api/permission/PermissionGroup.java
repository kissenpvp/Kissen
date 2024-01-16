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

    int delete();
}
