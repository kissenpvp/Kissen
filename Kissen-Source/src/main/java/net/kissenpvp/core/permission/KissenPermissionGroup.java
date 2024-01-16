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
import net.kissenpvp.core.api.permission.*;
import net.kissenpvp.core.api.permission.event.GroupMemberAddEvent;
import net.kissenpvp.core.api.user.User;
import net.kissenpvp.core.api.user.UserImplementation;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.savable.SerializableSavableHandler;
import net.kissenpvp.core.permission.event.KissenGroupMemberAddEvent;
import net.kissenpvp.core.permission.event.KissenGroupMemberRemoveEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;

public abstract class KissenPermissionGroup<T extends Permission> extends KissenGroupablePermissionEntry<T> implements PermissionGroup<T>
{
    @Override public @NotNull @Unmodifiable Set<String> getMember()
    {
        Set<String> member = new HashSet<>(getListNotNull("group_member"));
        UserImplementation userImplementation = KissenCore.getInstance().getImplementation(UserImplementation.class);
        member.addAll(userImplementation.getOnlineUser().stream().map(
                User::getPlayerClient).filter(
                player -> player.getRank().getSource().getName().equals(getPermissionID())).map(
                player -> player.getUniqueId().toString()).collect(Collectors.toUnmodifiableSet()));
        return Collections.unmodifiableSet(member);
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
            if (getPermissionID().equals(groupablePermissionEntry.getPermissionID()) || internalGroupCollector(new HashSet<>()).contains(groupablePermissionEntry.getPermissionID()))
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
            return getListNotNull("group_member").add(groupMemberAddEvent.getPermissionEntry().getPermissionID());
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

    @Override public @NotNull @Unmodifiable Set<UUID> getAffectedUsers()
    {
        Set<UUID> uuids = new HashSet<>();
        PermissionImplementation permissionImplementation = KissenCore.getInstance().getImplementation(
                PermissionImplementation.class);
        for (String member : getMember())
        {
            if (member.matches("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}"))
            {
                uuids.add(UUID.fromString(member));
                continue;
            }

            uuids.addAll(permissionImplementation.getPermissionGroupSavable(member).map(
                    PermissionEntry::getAffectedUsers).orElse(new HashSet<>()));
        }
        return uuids;
    }

    @Override public @NotNull @Unmodifiable Set<GroupablePermissionEntry<T>> getConnectedEntries()
    {
        Set<GroupablePermissionEntry<T>> groups = new HashSet<>();
        Class<PermissionImplementation> clazz = PermissionImplementation.class;
        PermissionImplementation permissionImplementation = KissenCore.getInstance().getImplementation(clazz);
        UserImplementation userImplementation = KissenCore.getInstance().getImplementation(UserImplementation.class);

        for (String member : getMember())
        {
            if (member.matches("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}"))
            {
                userImplementation.getOnlineUser(UUID.fromString(member)).ifPresent(user -> groups.add((GroupablePermissionEntry<T>) user));
                continue;
            }

            permissionImplementation.getPermissionGroupSavable(member).ifPresent(
                    group -> groups.add((PermissionGroup<T>) group));
            groups.addAll(permissionImplementation.getPermissionGroupSavable(member).map(
                    entry -> ((KissenPermissionGroup<T>) entry).getConnectedEntries()).orElse(new HashSet<>()));
        }
        return Collections.unmodifiableSet(groups);
    }

    @Override
    public void permissionUpdate()
    {
        getConnectedEntries().forEach(PermissionEntry::permissionUpdate);
    }

    @Override public SerializableSavableHandler getSerializableSavableHandler()
    {
        return new SerializablePermissionGroupHandler(getPermissionID());
    }
}
