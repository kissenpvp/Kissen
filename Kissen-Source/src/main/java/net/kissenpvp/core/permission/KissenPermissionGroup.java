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

package net.kissenpvp.core.permission;

import net.kissenpvp.core.api.event.EventImplementation;
import net.kissenpvp.core.api.permission.GroupablePermissionEntry;
import net.kissenpvp.core.api.permission.Permission;
import net.kissenpvp.core.api.permission.PermissionGroup;
import net.kissenpvp.core.api.permission.PermissionGroupConflictException;
import net.kissenpvp.core.api.permission.event.GroupMemberAddEvent;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.savable.SerializableSavableHandler;
import net.kissenpvp.core.permission.event.KissenGroupMemberAddEvent;
import net.kissenpvp.core.permission.event.KissenGroupMemberRemoveEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class KissenPermissionGroup<T extends Permission> extends KissenGroupablePermissionEntry<T> implements PermissionGroup<T>
{
    @Override public @NotNull @Unmodifiable Set<String> getMember()
    {
        return Set.copyOf(getListNotNull("group_member"));
    }

    @Override public boolean addMember(@NotNull GroupablePermissionEntry<?> groupablePermissionEntry) throws PermissionGroupConflictException
    {
        boolean added = addInternalMember(groupablePermissionEntry);
        if (added)
        {
            groupablePermissionEntry.permissionUpdate();
            return true;
        }
        return false;
    }

    private boolean addInternalMember(@NotNull GroupablePermissionEntry<?> groupablePermissionEntry) throws PermissionGroupConflictException
    {
        if (groupablePermissionEntry instanceof PermissionGroup<?>)
        {
            if (internalGroupCollector(new HashSet<>()).contains(groupablePermissionEntry.getPermissionID()))
            {
                throw new PermissionGroupConflictException();
            }
        }

        if (getListNotNull("group_member").contains(groupablePermissionEntry.getPermissionID()))
        {
            return false;
        }

        GroupMemberAddEvent groupMemberAddEvent = new KissenGroupMemberAddEvent(this, groupablePermissionEntry);
        if(KissenCore.getInstance().getImplementation(EventImplementation.class).call(groupMemberAddEvent))
        {
            if (!getListNotNull("group_member").contains(groupablePermissionEntry.getPermissionID()))
            {
                return getListNotNull("group_member").add(groupablePermissionEntry.getPermissionID());
            }

            return setListValue("group_member", groupablePermissionEntry.getPermissionID()).contains(groupablePermissionEntry.getPermissionID());
        }
        return false;
    }

    @Override public boolean removeMember(@NotNull GroupablePermissionEntry<?> groupablePermissionEntry)
    {
        boolean removed = removeInternalMember(groupablePermissionEntry);
        if (removed)
        {
            groupablePermissionEntry.permissionUpdate();
            return true;
        }
        return false;
    }

    private boolean removeInternalMember(@NotNull GroupablePermissionEntry<?> groupablePermissionEntry)
    {
        if (!containsList("group_member") || !getListNotNull("group_member").contains(groupablePermissionEntry.getPermissionID()))
        {
            return false;
        }
        KissenGroupMemberRemoveEvent kissenGroupMemberRemoveEvent = new KissenGroupMemberRemoveEvent(this, groupablePermissionEntry);
        if(KissenCore.getInstance().getImplementation(EventImplementation.class).call(kissenGroupMemberRemoveEvent))
        {
            return getListNotNull("group_member").remove(groupablePermissionEntry.getPermissionID());
        }
        return false;
    }

    @Override public @NotNull @Unmodifiable Set<UUID> getAffectedPermissionPlayer()
    {
        return getMember().stream().filter(member ->
        {
            try
            {
                UUID uuid = UUID.fromString(member);
                return true;
            }
            catch (IllegalArgumentException ignored) { }
            return false;
        }).map(UUID::fromString).collect(Collectors.toUnmodifiableSet());
    }

    @Override public SerializableSavableHandler getSerializableSavableHandler()
    {
        return new SerializablePermissionGroupHandler(getPermissionID());
    }
}
