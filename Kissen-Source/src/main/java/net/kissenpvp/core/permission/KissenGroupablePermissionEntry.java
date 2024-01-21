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
import net.kissenpvp.core.time.TemporalMeasureNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

@Getter
public abstract class KissenGroupablePermissionEntry<T extends Permission> extends KissenPermissionEntry<T> implements PermissionCollector<T>
{
    private volatile Set<T> cache;
    private volatile List<String> groupCache;

    public KissenGroupablePermissionEntry()
    {
        this.cache = null;
    }

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
    public void permissionUpdate()
    {
        cache = null;
        groupCache = null;
    }

    @Override
    public int wipeGroups() {
        return (int) getPermissionGroups().stream().filter(permissiongroup -> permissiongroup.removeMember(this)).count();
    }

    @Override
    public synchronized @NotNull @Unmodifiable Set<T> getPermissionList() {
        if (cache == null)
        {
            cache = new HashSet<>();
            cache.addAll(permissionCollector());
        }
        return Collections.unmodifiableSet(cache);
    }

    @Override
    public boolean inGroup(@NotNull PermissionGroup<T> permissionGroup) {
        return permissionGroup.getMember().contains(getPermissionID());
    }

    @Override
    public @NotNull Set<T> permissionCollector()
    {
        System.out.println(getPermissionID() + " " + getPermissionGroups().stream().map(PermissionEntry::getPermissionID).toList());
        Set<T> permissions = new KissenPermissionSet<>(getOwnPermissions());
        getPermissionGroups().stream().map(PermissionGroup::getPermissionList).forEach(permissions::addAll);
        return permissions;
    }

    @Override
    public @NotNull @Unmodifiable List<String> groupCollector()
    {
        if(groupCache == null)
        {
            Comparator<PermissionGroup<T>> compareID = Comparator.comparing(PermissionGroup::getPermissionID);
            List<PermissionGroup<T>> sorted = getPermissionGroups().stream().sorted(compareID).toList();
            LinkedHashSet<String> permissionGroups = new LinkedHashSet<>(sorted.stream().map(PermissionGroup::getPermissionID).toList());
            sorted.forEach(group -> permissionGroups.addAll(((PermissionCollector<T>) group).groupCollector()));
            groupCache = new ArrayList<>(permissionGroups.stream().toList());
        }
        return groupCache;
    }

    @Override
    public @NotNull SavableList putList(@NotNull String key, @Nullable List<String> value) {
        SavableList result = super.putList(key, value);
        if (key.equals("permission_group_list")) {
            permissionUpdate();
        }
        return result;
    }
}
