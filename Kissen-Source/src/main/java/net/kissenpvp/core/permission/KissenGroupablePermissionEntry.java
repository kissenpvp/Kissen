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
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.permission.Permission;
import net.kissenpvp.core.api.permission.PermissionEntry;
import net.kissenpvp.core.api.permission.PermissionGroup;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.time.TemporalMeasureNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public abstract class KissenGroupablePermissionEntry<T extends Permission> extends KissenPermissionEntry<T> implements PermissionCollector<T>
{

    private volatile Set<T> cachedPermissions;

    @Override
    public @NotNull T setPermission(@NotNull String permission, boolean value) throws EventCancelledException
    {
        Optional<T> currentPermission = getOwnPermission(permission);
        return currentPermission.map(current -> setPermission(current, value)).orElseGet(
                () -> setPermission(new KissenPermissionNode(permission, this, value, new TemporalMeasureNode())));
    }

    @Override
    public @NotNull @Unmodifiable Set<T> getOwnPermissions() {
        return super.getPermissionList();
    }

    @Override
    public @NotNull @Unmodifiable Optional<T> getOwnPermission(@NotNull String permission)
    {
        return getOwnPermissions().stream().filter(
                currentPermission -> currentPermission.getName().equals(permission)).findFirst();
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
        return permissionCollector();
    }

    @Override
    public @NotNull Set<T> permissionCollector()
    {
        Set<T> permissions = new KissenPermissionSet<>(getOwnPermissions());
        permissions.addAll(getPermissionStream().collect(Collectors.toSet()));
        return permissions;
    }

    @Override
    public @NotNull Set<String> internalGroupCollector(@NotNull Set<String> blacklistedGroups) {
        if (blacklistedGroups.contains(getPermissionID())) {
            return new HashSet<>();
        }
        blacklistedGroups.add(getPermissionID());
        Set<String> groupList = new HashSet<>(getPermissionGroups().stream().map(PermissionEntry::getPermissionID).filter(permissionID -> !blacklistedGroups.contains(permissionID)).collect(Collectors.toUnmodifiableSet()));
        for (PermissionCollector<T> permissionGroup : getPermissionGroups().stream().map(permissionGroup -> ((PermissionCollector<T>) permissionGroup)).collect(Collectors.toSet())) {
            if (!blacklistedGroups.contains(permissionGroup.getPermissionID())) {
                groupList.addAll(permissionGroup.internalGroupCollector(blacklistedGroups));
            }
        }
        return groupList;
    }

    @Override
    public @NotNull SavableList putList(@NotNull String key, @Nullable List<String> value) {
        SavableList result = super.putList(key, value);
        if (key.equals("permission_group_list")) {
            permissionUpdate();
        }
        return result;
    }

    @Override
    public void permissionUpdate() {
        clearCache();
    }


    public void clearCache()
    {
        cachedPermissions = null;
        KissenCore.getInstance().getLogger().debug("{}s cache has been cleared.", getPermissionID());
    }

    private @NotNull Stream<T> getPermissionStream()
    {
        return parseGroups().stream().flatMap(group -> group.permissionCollector().stream());
    }

    private @NotNull @Unmodifiable List<PermissionCollector<T>> parseGroups()
    {
        Function<PermissionGroup<T>, PermissionCollector<T>> mapper = (permissionGroup) -> ((PermissionCollector<T>) permissionGroup);
        Stream<PermissionGroup<T>> groupStream = getPermissionGroups().stream();
        return groupStream.map(mapper).sorted(
                (o1, o2) -> CharSequence.compare(o1.getPermissionID(), o2.getPermissionID())).toList();
    }
}
