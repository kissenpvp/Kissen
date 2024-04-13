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
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.permission.AbstractGroupablePermissionEntry;
import net.kissenpvp.core.api.permission.AbstractPermission;
import net.kissenpvp.core.api.permission.AbstractPermissionGroup;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.api.time.TemporalData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Stream;

@Getter
public abstract class KissenGroupablePermissionEntry<T extends AbstractPermission> extends KissenPermissionEntry<T> implements AbstractGroupablePermissionEntry<T>
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
                () -> setPermission(new PermissionNode(permission, this, value, new TemporalData())));
    }

    @Override
    public @NotNull @Unmodifiable Set<T> getOwnPermissions() {
        return super.getPermissionList();
    }

    @Override
    public @NotNull @Unmodifiable Optional<T> getOwnPermission(@NotNull String permission)
    {
        return getOwnPermissions().stream().filter(current -> current.getName().equals(permission)).findFirst();
    }

    @Override
    public void permissionUpdate()
    {
        cache = null;
        groupCache = null;
    }

    @Override
    public int wipeGroups() {
        return (int) getOwnPermissionGroups().stream().filter(permissiongroup -> permissiongroup.removeMember(this)).count();
    }

    @Override
    public @NotNull List<AbstractPermissionGroup<T>> getOwnPermissionGroups()
    {
        InternalPermissionImplementation<T> permission = KissenCore.getInstance().getImplementation(InternalPermissionImplementation.class);
        Stream<AbstractPermissionGroup<T>> groupStream = permission.getInternalGroups().stream();
        return groupStream.filter(group -> group.getOwnMember().contains(getPermissionID())).toList();
    }

    @Override
    public @NotNull @Unmodifiable List<AbstractPermissionGroup<T>> getPermissionGroups()
    {
        List<AbstractPermissionGroup<T>> ownGroups = getOwnPermissionGroups();
        List<AbstractPermissionGroup<T>> groups = new ArrayList<>(ownGroups);
        groups.addAll(ownGroups.stream().flatMap(group -> group.getPermissionGroups().stream()).toList());
        return groups;
    }

    @Override
    public synchronized @NotNull @Unmodifiable Set<T> getPermissionList() {
        if (cache == null)
        {
            Set<T> permissions = new KissenPermissionSet<>(getOwnPermissions());
            getOwnPermissionGroups().stream().map(AbstractPermissionGroup::getPermissionList).forEach(permissions::addAll);
            cache = permissions;
        }
        return Collections.unmodifiableSet(cache);
    }

    @Override
    public boolean inGroup(@NotNull AbstractPermissionGroup<T> permissionGroup) {
        return permissionGroup.getMember().contains(getPermissionID());
    }

/*    @Override //TODO
    public <X> @Nullable Object putList(@NotNull String key, @Nullable Collection<X> value) {
        Object result = super.putList(key, value);
        if (key.equals("permission_group_list")) {
            permissionUpdate();
        }
        return result;
    }*/

    private @NotNull InternalPermissionImplementation<T> getImplementation()
    {
        return KissenCore.getInstance().getImplementation(InternalPermissionImplementation.class);
    }
}
