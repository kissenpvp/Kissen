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

import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.permission.AbstractPermission;
import net.kissenpvp.core.api.permission.AbstractPermissionEntry;
import net.kissenpvp.core.api.permission.event.PermissionEntrySetPermissionEvent;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.api.database.DataWriter;
import net.kissenpvp.core.database.savable.KissenSavable;
import net.kissenpvp.core.event.EventImplementation;
import net.kissenpvp.core.permission.event.KissenPermissionEntrySetPermissionEvent;
import net.kissenpvp.core.api.time.TemporalData;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class KissenPermissionEntry<T extends AbstractPermission> extends KissenSavable implements AbstractPermissionEntry<T> {
    @Override
    public @NotNull String getPermissionID() {
        return getRawID();
    }

    @Override
    public @NotNull Component displayName() {
        return Component.text(getRawID());
    }

    @Override
    public @NotNull T setPermission(@NotNull String permission) throws EventCancelledException {
        return setPermission(permission, true);
    }

    @Override
    public @NotNull T setPermission(@NotNull String permission, boolean value) throws EventCancelledException {
        return getPermission(permission).map(current -> setPermission(current, value)).orElseGet(() -> {
            PermissionNode newPermission = new PermissionNode(permission, this, value, new TemporalData());
            return setPermission(permission);
        });
    }

    @Override
    public @NotNull T setPermission(@NotNull T permission) throws EventCancelledException {
        if (!permission.getOwner().equals(this)) {
            throw new IllegalArgumentException("The specified permission owner does not match this object.");
        }

        return setPermission(new PermissionNode(permission));
    }

    @Override
    public boolean unsetPermission(@NotNull String permission) {
        return getRepository().getList("permission_list", PermissionNode.class).map(list -> list.removeIf(node -> node.name().equals(permission))).orElse(false);
    }

    @Override
    public int wipePermissions() {
        return getRepository().getList("permission_list", PermissionNode.class).map(list -> {
            int count = list.size();
            list.clear();
            return count;
        }).orElse(0);
    }

    @Override
    public @NotNull @Unmodifiable Set<T> getPermissionList() {
        Stream<PermissionNode> permissionNodes = getRepository().getListNotNull("permission_list", PermissionNode.class).stream();
        return permissionNodes.map(this::translatePermission).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @NotNull Optional<T> getPermission(@NotNull String permission) {
        Predicate<T> isSearched = currentPermission -> currentPermission.getName().equals(permission);
        return getPermissionList().stream().filter(isSearched).findFirst();
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return getInternalPermission(permission).orElse(false);
    }

    /**
     * Retrieves the boolean value indicating whether the specified user has the internal permission.
     * <p>
     * This method uses a matching function and comparator to find the relevant internal permissions,
     * sorts them by name, and returns the value of the latest matching permission. The returned value
     * is useful for checking whether a user has a particular permission.
     *
     * @param permission the name of the permission to check. Must not be null.
     * @return An Optional containing the Boolean value of the internal permission if found,
     *         or an empty Optional if no matching internal permission is found.
     * @throws NullPointerException if the provided permission is null.
     *
     * @see #setPermission(AbstractPermission, boolean)
     */
    public @NotNull Optional<Boolean> getInternalPermission(@NotNull String permission) {

        Function<T, Stream<T>> matching = internalPermission -> matcher(permission, internalPermission).stream();
        Comparator<T> sortByName = (o1, o2) -> CharSequence.compare(o1.getName(), o2.getName());
        List<T> permissions = getPermissionList().stream().flatMap(matching).sorted(sortByName).toList();

        if (permissions.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(permissions.get(permissions.size() - 1).getValue());
    }

    /**
     * Sets the value of the specified permission and ensures its ownership matches this object.
     * <p>
     * This method checks if the owner of the provided permission is the same as this object.
     * If not, it throws an IllegalArgumentException. If the permission value differs from the
     * specified value, it updates the permission value. The modified permission is then returned.
     *
     * @param permission The permission to set. Must not be null.
     * @param value      The boolean value to set for the permission.
     * @return The modified permission object after setting the new value.
     * @throws IllegalArgumentException if the owner of the provided permission does not match this object.
     * @throws EventCancelledException  if the operation is canceled due to an event (checked exception).
     * @throws NullPointerException     if the provided permission is null.
     */
    protected @NotNull T setPermission(@NotNull T permission, boolean value) throws EventCancelledException {
        if (!permission.getOwner().equals(this)) {
            throw new IllegalArgumentException("The specified permission owner does not match this object.");
        }
        if (permission.getValue() != value) {
            permission.setValue(value);
        }
        return permission;
    }

    /**
     * Matches a permission string against an internal permission.
     *
     * <p>This method checks whether the given permission string matches the specified internal permission.
     * It supports wildcard matching using '*' and '?' characters in the internal permission string.</p>
     *
     * <p>The permission string is compared against the internal permission using a pattern matching algorithm.
     * The algorithm takes into account the wildcard characters '*' and '?' in the internal permission string,
     * allowing for flexible and customizable permission matching.</p>
     *
     * <p>The '*' character matches any sequence of characters (including an empty sequence),
     * while the '?' character matches any single character. These wildcard characters can be used to
     * represent unknown or variable parts in the permission string.</p>
     *
     * <p>For example, if the internal permission is "com.example.myapp.*" and the permission string is
     * "com.example.myapp.admin", the method will return the internal permission object, as the
     * permission string matches the internal permission pattern.</p>
     *
     * <p>On the other hand, if the internal permission is "com.example.myapp.read" and the permission string
     * is "com.example.myapp.write", the method will return null, indicating that the permission string
     * does not match the internal permission pattern.</p>
     *
     * @param permission         The permission string to match against the internal permission.
     *                           It should not be null.
     * @param internalPermission The internal permission object to compare with the permission string.
     *                           It should not be null.
     * @return The internal permission object if the permission string matches the
     * internal permission, or null if it doesn't.
     * @throws NullPointerException if either the permission or internalPermission parameter is null.
     */
    public @NotNull Optional<T> matcher(@NotNull String permission, @NotNull T internalPermission) {
        int testedIndex = 0, givenIndex = 0, testedWildcardIndex = -1, givenWildcardIndex = -1;

        while (givenIndex < permission.length()) {
            if (testedIndex < internalPermission.getName().length() && internalPermission.getName().charAt(testedIndex) == '*') {
                testedWildcardIndex = testedIndex;
                givenWildcardIndex = givenIndex;
                testedIndex++;
                continue;
            }

            if (testedIndex < internalPermission.getName().length() && (internalPermission.getName().charAt(testedIndex) == permission.charAt(givenIndex) || internalPermission.getName().charAt(testedIndex) == '?')) {
                testedIndex++;
                givenIndex++;
                continue;
            }

            if (testedWildcardIndex != -1) {
                testedIndex = testedWildcardIndex + 1;
                givenIndex = givenWildcardIndex + 1;
                givenWildcardIndex++;
                continue;
            }

            return Optional.empty();
        }

        while (testedIndex < internalPermission.getName().length() && internalPermission.getName().charAt(testedIndex) == '*') {
            testedIndex++;
        }

        return testedIndex == internalPermission.getName().length() && givenIndex == permission.length() ? Optional.of(internalPermission) : Optional.empty();
    }

    /**
     * Sets a permission for the specified Kissen permission node.
     * <p>
     * This method attempts to set a permission for the given Kissen permission node. It triggers an event to notify other components
     * about the permission change and checks if the event is allowed to proceed. If the event is allowed, the permission is internally set
     * using the provided permission node. If the event is cancel by any listener, an {@link EventCancelledException} is thrown.
     *
     * @param permissionNode The Kissen permission node for which the permission should be set. Must not be null.
     * @return A non-null value representing the result of setting the permission.
     * @throws EventCancelledException If the permission change event is cancel by any listener.
     */
    protected @NotNull T setPermission(@NotNull PermissionNode permissionNode) throws EventCancelledException {
        PermissionEntrySetPermissionEvent permissionEntrySetPermissionEvent = new KissenPermissionEntrySetPermissionEvent(translatePermission(permissionNode, record -> {/* ignored */}), getPermission(permissionNode.name()).isPresent());

        if (KissenCore.getInstance().getImplementation(EventImplementation.class).call(permissionEntrySetPermissionEvent)) {
            return internSetPermission(((KissenPermission) permissionEntrySetPermissionEvent.getPermission()).getKissenPermissionNode());
        }
        throw new EventCancelledException();
    }

    /**
     * Internally sets a permission for the specified Kissen permission node.
     * <p>
     * This method performs an internal operation to set a permission for the given Kissen permission node within the context of the system.
     * It manages the storage and handling of permission data, ensuring that the provided permission node is correctly stored and managed.
     *
     * @param permissionNode The Kissen permission node for which the permission should be set. Must not be null.
     * @return A non-null value representing the result of internally setting the permission.
     */
    private @NotNull T internSetPermission(@NotNull PermissionNode permissionNode) {
        getRepository().getListNotNull("permission_list", PermissionNode.class).replaceOrInsert(permissionNode);
        return translatePermission(permissionNode, permissionWriter());
    }

    /**
     * Translates a Kissen permission node into the appropriate permission representation.
     * <p>
     * This method serves as a convenient wrapper, utilizing the provided permission writer obtained from {@link #permissionWriter()},
     * to perform the actual translation of the given {@link PermissionNode}. The translated permission is then returned.
     *
     * @param permissionNode the{@link PermissionNode} to be translated. Must not be null.
     * @return A non-null value representing the translated permission result.
     *
     * @see #translatePermission(PermissionNode, DataWriter)
     */
    private @NotNull T translatePermission(@NotNull PermissionNode permissionNode) {
        return translatePermission(permissionNode, permissionWriter());
    }

    /**
     * Translates a Kissen permission node into the appropriate permission representation.
     * <p>
     * This method is responsible for translating a given Kissen permission node into the suitable permission representation within the context
     * of the system. It may involve formatting, conversion, or other transformations required for accurate permission management.
     *
     * @param permissionNode The Kissen permission node to be translated. Must not be null.
     * @param dataWriter     An optional data writer for storing the translated permission. Can be null.
     * @return A non-null value representing the translated permission result.
     */
    protected abstract @NotNull T translatePermission(@NotNull PermissionNode permissionNode, @Nullable DataWriter<PermissionNode> dataWriter);


    /**
     * Retrieves a data writer for saving changes made to permission data.
     * <p>
     * This method provides a data writer that facilitates the process of saving changes to permission data after it has been modified.
     * It returns a non-null data writer instance that can be used to store changes in the context of the system's permission management.
     *
     * @return A non-null data writer instance for saving permission changes.
     */
    private @NotNull DataWriter<PermissionNode> permissionWriter() {
        return this::internSetPermission;
    }

    @Override
    public int softDelete() throws BackendException {
        int rows = super.softDelete();
        permissionUpdate();
        return rows;
    }
}
