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

import net.kissenpvp.core.api.database.meta.Meta;
import net.kissenpvp.core.api.database.savable.SavableMap;
import net.kissenpvp.core.api.permission.*;
import net.kissenpvp.core.api.user.User;
import net.kissenpvp.core.api.user.UserImplementation;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.KissenTable;
import net.kissenpvp.core.database.savable.KissenSavableMap;
import net.kissenpvp.core.database.savable.SerializableSavableHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class KissenPermissionGroup<T extends AbstractPermission> extends KissenGroupablePermissionEntry<String, T> implements AbstractPermissionGroup<T> {

    @Override
    public @NotNull @Unmodifiable Set<String> getMember() {
        Set<String> member = new HashSet<>();
        memberTransform().apply(getOwnMember()).forEach(current -> {
            member.add(current.getPermissionID());
            if (current instanceof AbstractPermissionGroup<T> permissionGroup) {
                member.addAll(((AbstractPermissionGroup<T>) current).getMember());
            }
        });
        return member;
    }

    @Override
    public @NotNull @Unmodifiable Set<String> getOwnMember() {
        UserImplementation userImplementation = KissenCore.getInstance().getImplementation(UserImplementation.class);
        Set<String> member = new HashSet<>(getRepository().getListNotNull("group_member", String.class));

        // add users with rank called like this group to members
        Predicate<User> matches = user -> user.getPlayerClient().getRank().getSource().getName().equals(getPermissionID());
        Function<User, String> transform = user -> ((AbstractPermissionEntry<T>) user).getPermissionID();
        Stream<User> userStream = userImplementation.getOnlineUser().stream();

        member.addAll(userStream.filter(matches).map(transform).collect(Collectors.toUnmodifiableSet()));

        return Set.copyOf(member);
    }

    @Override
    public boolean addMember(@NotNull AbstractGroupablePermissionEntry<?> groupablePermissionEntry) throws PermissionGroupConflictException {
        boolean added = addInternalMember(groupablePermissionEntry);
        if (added) {
            groupablePermissionEntry.permissionUpdate();
            return true;
        }
        return false;
    }

    private boolean addInternalMember(@NotNull AbstractGroupablePermissionEntry<?> groupablePermissionEntry) throws PermissionGroupConflictException {
        if (groupablePermissionEntry instanceof AbstractPermissionGroup<?>) {
            if (getPermissionID().equals(groupablePermissionEntry.getPermissionID()) || getPermissionGroups().contains(groupablePermissionEntry)) {
                throw new PermissionGroupConflictException();
            }
        }

        if (getRepository().getListNotNull("group_member", String.class).contains(groupablePermissionEntry.getPermissionID())) {
            return false;
        }

        return getRepository().getListNotNull("group_member", String.class).add(groupablePermissionEntry.getPermissionID());
    }

    @Override
    public boolean removeMember(@NotNull AbstractGroupablePermissionEntry<?> groupablePermissionEntry) {
        boolean removed = removeInternalMember(groupablePermissionEntry);
        if (removed) {
            groupablePermissionEntry.permissionUpdate();
            return true;
        }
        return false;
    }

    private boolean removeInternalMember(@NotNull AbstractGroupablePermissionEntry<?> groupablePermissionEntry) {
        SavableMap repo = getRepository();
        if (!repo.containsList("group_member") || !repo.getListNotNull("group_member", String.class).contains(groupablePermissionEntry.getPermissionID())) {
            return false;
        }
        return repo.getListNotNull("group_member", String.class).remove(groupablePermissionEntry.getPermissionID());
    }

    @Override
    public @NotNull @Unmodifiable Set<UUID> getAffectedUsers() {
        Set<UUID> uuids = new HashSet<>();
        InternalPermissionImplementation<?> permissionImplementation = KissenCore.getInstance().getImplementation(InternalPermissionImplementation.class);
        for (String member : getMember()) {
            if (member.matches("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}")) {
                uuids.add(UUID.fromString(member));
                continue;
            }

            uuids.addAll(permissionImplementation.getPermissionGroupSavable(member).map(AbstractPermissionEntry::getAffectedUsers).orElse(new HashSet<>()));
        }
        return uuids;
    }

    @Override
    public @NotNull @Unmodifiable Set<AbstractGroupablePermissionEntry<T>> getConnectedEntries() {
        return memberTransform().apply(getOwnMember());
    }

    @Contract(pure = true, value = "-> new")
    private @NotNull Function<Set<String>, Set<AbstractGroupablePermissionEntry<T>>> memberTransform() {
        InternalPermissionImplementation<T> permissionImplementation = KissenCore.getInstance().getImplementation(InternalPermissionImplementation.class);
        UserImplementation userImplementation = KissenCore.getInstance().getImplementation(UserImplementation.class);
        return (memberList) -> {
            Set<AbstractGroupablePermissionEntry<T>> entries = new HashSet<>();
            for (String member : memberList) {
                if (member.matches("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}")) {
                    Consumer<User> add = user -> entries.add((AbstractGroupablePermissionEntry<T>) user);
                    userImplementation.getOnlineUser(UUID.fromString(member)).ifPresent(add);
                    continue;
                }

                Consumer<AbstractPermissionGroup<?>> add = group -> entries.add((AbstractPermissionGroup<T>) group);
                permissionImplementation.getInternalGroups().stream().filter(group -> group.getPermissionID().equals(member)).findFirst().ifPresent(add);
            }
            return entries;
        };
    }

    @Override
    protected @NotNull SavableMap createRepository(@Nullable Map<String, Object> data) {
        Meta meta = KissenCore.getInstance().getImplementation(InternalKissenPermissionImplementation.class).getMeta();
        return new KissenSavableMap(getDatabaseID(), meta, Objects.requireNonNullElseGet(data, meta.getData(getDatabaseID())::join));
    }

    @Override
    public @NotNull KissenTable getTable() {
        return KissenCore.getInstance().getImplementation(InternalKissenPermissionImplementation.class).getTable();
    }
}
