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

import lombok.Getter;
import net.kissenpvp.core.api.database.savable.list.SavableList;
import net.kissenpvp.core.api.permission.Permission;
import net.kissenpvp.core.api.permission.PermissionEntry;
import net.kissenpvp.core.api.permission.PermissionGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class KissenGroupablePermissionEntry<T extends Permission> extends KissenPermissionEntry<T> implements InternalGroupablePermissionEntry<T> {

    @Getter
    private Set<T> cachedPermissions;

    @Override
    public @NotNull @Unmodifiable Set<T> getOwnPermissions() {
        return super.getPermissionList();
    }

    @Override
    public int wipeGroups() {
        return (int) getPermissionGroups().stream().filter(permissiongroup -> permissiongroup.removeMember(this)).count();
    }

    @Override
    public synchronized @NotNull @Unmodifiable Set<T> getPermissionList() {
        if (cachedPermissions == null) {
            cachedPermissions = calculatePermissions();
        }
        return Collections.unmodifiableSet(cachedPermissions);
    }

    @Override
    public boolean inGroup(@NotNull PermissionGroup<T> permissionGroup) {
        return permissionGroup.getMember().contains(getPermissionID());
    }

    public @Unmodifiable @NotNull Set<T> calculatePermissions() {
        return internalPermissionCollector(new HashSet<>(), new HashSet<>());
    }

    @Override
    public @NotNull Set<T> internalPermissionCollector(@NotNull Set<String> blacklistedGroups, @NotNull Set<String> blacklistedPermissions) {
        if (blacklistedGroups.contains(getPermissionID())) {
            return new HashSet<>();
        }
        Set<T> permissions = super.getPermissionList().stream().filter(permission -> !blacklistedPermissions.contains(permission.getName())).collect(Collectors.toSet());

        blacklistedGroups.add(getPermissionID());
        blacklistedPermissions.addAll(permissions.stream().map(Permission::getName).collect(Collectors.toSet()));
        for (InternalGroupablePermissionEntry<T> permissionGroup : getPermissionGroups().stream().map(permissionGroup -> ((InternalGroupablePermissionEntry<T>) permissionGroup)).collect(Collectors.toSet())) {
            if (!blacklistedGroups.contains(permissionGroup.getPermissionID())) {
                permissions.addAll(permissionGroup.internalPermissionCollector(blacklistedGroups, blacklistedPermissions));
            }
        }
        return permissions;
    }

    @Override
    public @NotNull Set<String> internalGroupCollector(@NotNull Set<String> blacklistedGroups) {
        if (blacklistedGroups.contains(getPermissionID())) {
            return new HashSet<>();
        }
        blacklistedGroups.add(getPermissionID());
        Set<String> groupList = new HashSet<>(getPermissionGroups().stream().map(PermissionEntry::getPermissionID).filter(permissionID -> !blacklistedGroups.contains(permissionID)).collect(Collectors.toUnmodifiableSet()));
        for (InternalGroupablePermissionEntry<T> permissionGroup : getPermissionGroups().stream().map(permissionGroup -> ((InternalGroupablePermissionEntry<T>) permissionGroup)).collect(Collectors.toSet())) {
            if (!blacklistedGroups.contains(permissionGroup.getPermissionID())) {
                groupList.addAll(permissionGroup.internalGroupCollector(blacklistedGroups));
            }
        }
        return groupList;
    }

    @Override
    public @NotNull SavableList putList(@NotNull String key, @Nullable List<String> value) {
        if (key.equals("permission_group_list")) {
            permissionUpdate();
        }
        return super.putList(key, value);
    }

    @Override
    public void permissionUpdate() {
        cachedPermissions = null;
    }
}
