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
import net.kissenpvp.core.api.database.savable.list.SavableList;
import net.kissenpvp.core.api.database.savable.list.SavableRecordList;
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.event.EventImplementation;
import net.kissenpvp.core.api.permission.Permission;
import net.kissenpvp.core.api.permission.PermissionEntry;
import net.kissenpvp.core.api.permission.event.PermissionEntrySetPermissionEvent;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.DataWriter;
import net.kissenpvp.core.database.savable.KissenSavable;
import net.kissenpvp.core.permission.event.KissenPermissionEntryClearEvent;
import net.kissenpvp.core.permission.event.KissenPermissionEntrySetPermissionEvent;
import net.kissenpvp.core.permission.event.KissenPermissionEntryUnsetPermissionEvent;
import net.kissenpvp.core.time.TemporalMeasureNode;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;

public abstract class KissenPermissionEntry<T extends Permission> extends KissenSavable implements PermissionEntry<T> {
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
        Optional<T> currentPermission = getPermission(permission);
        return currentPermission.map(current -> setPermission(current, value)).orElseGet(
                () -> setPermission(new KissenPermissionNode(permission, this, value, new TemporalMeasureNode())));
    }

    @Override
    public @NotNull T setPermission(@NotNull T permission) throws EventCancelledException {
        if (!permission.getOwner().equals(this)) {
            throw new IllegalArgumentException("The specified permission owner does not match this object.");
        }

        return setPermission(new KissenPermissionNode(permission));
    }

    @Override
    public boolean unsetPermission(@NotNull String permission) {
        if (containsList("permission_list")) {
            Optional<T> extracted = getPermission(permission);
            if (extracted.isPresent() && KissenCore.getInstance()
                    .getImplementation(EventImplementation.class)
                    .call(new KissenPermissionEntryUnsetPermissionEvent(extracted.get()))) {
                return getListNotNull("permission_list").toRecordList(KissenPermissionNode.class)
                        .removeIfRecord(currentPermission -> currentPermission.name().equals(permission));
            }
        }
        return false;
    }

    @Override
    public int wipePermissions() {
        if (containsList("permission_list") && !getListNotNull("permission_list").isEmpty()) {
            KissenPermissionEntryClearEvent kissenPermissionEntryClearEvent = new KissenPermissionEntryClearEvent(this);
            if (KissenCore.getInstance()
                    .getImplementation(EventImplementation.class)
                    .call(kissenPermissionEntryClearEvent)) {
                SavableList savableList = getListNotNull("permission_list");
                int entries = savableList.size();
                savableList.clear();
                return entries;
            }
        }

        return 0;
    }

    @Override
    public @NotNull @Unmodifiable Set<T> getPermissionList() {
        return getListNotNull("permission_list").toRecordList(KissenPermissionNode.class)
                .toRecordList()
                .stream()
                .map(kissenPermissionNode -> translatePermission(kissenPermissionNode, getSaveChanges()))
                .collect(Collectors.toSet());
    }

    @Override
    public @NotNull Optional<T> getPermission(@NotNull String permission) {
        return getPermissionList().stream()
                .filter(currentPermission -> currentPermission.getName().equals(permission))
                .findFirst();
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        List<Permission> permissions = new ArrayList<>(
                getPermissionList().stream().map(internalPermission -> matcher(permission, internalPermission)).filter(
                        Objects::nonNull).toList());
        if (permissions.isEmpty()) {
            return false;
        }
        permissions.sort((o1, o2) -> CharSequence.compare(o1.getName(), o2.getName()));
        return permissions.get(permissions.size() - 1).getValue();
    }

    @Override
    public @NotNull SavableList putList(@NotNull String key, @Nullable List<String> value) {
        SavableList result = super.putList(key, value);
        if (key.equals("permission_list")) {
            permissionUpdate();
        }
        return result;
    }

    protected @NotNull T setPermission(@NotNull T permission, boolean value) throws EventCancelledException
    {
        if (!permission.getOwner().equals(this))
        {
            throw new IllegalArgumentException("The specified permission owner does not match this object.");
        }

        if (permission.getValue() != value)
        {
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
    public @Nullable T matcher(@NotNull String permission, @NotNull T internalPermission)
    {
        int testedIndex = 0, givenIndex = 0, testedWildcardIndex = -1, givenWildcardIndex = -1;

        while (givenIndex < permission.length()) {
            if (testedIndex < internalPermission.getName().length() && internalPermission.getName()
                    .charAt(testedIndex) == '*') {
                testedWildcardIndex = testedIndex;
                givenWildcardIndex = givenIndex;
                testedIndex++;
                continue;
            }

            if (testedIndex < internalPermission.getName().length() && (internalPermission.getName()
                    .charAt(testedIndex) == permission.charAt(givenIndex) || internalPermission.getName()
                    .charAt(testedIndex) == '?')) {
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

            return null;
        }

        while (testedIndex < internalPermission.getName().length() && internalPermission.getName()
                .charAt(testedIndex) == '*') {
            testedIndex++;
        }

        return testedIndex == internalPermission.getName().length() && givenIndex == permission.length() ? internalPermission : null;
    }

    /**
     * Sets a permission for the specified Kissen permission node.
     * <p>
     * This method attempts to set a permission for the given Kissen permission node. It triggers an event to notify other components
     * about the permission change and checks if the event is allowed to proceed. If the event is allowed, the permission is internally set
     * using the provided permission node. If the event is cancel by any listener, an {@link EventCancelledException} is thrown.
     *
     * @param kissenPermissionNode The Kissen permission node for which the permission should be set. Must not be null.
     * @return A non-null value representing the result of setting the permission.
     * @throws EventCancelledException If the permission change event is cancel by any listener.
     */
    protected @NotNull T setPermission(@NotNull KissenPermissionNode kissenPermissionNode) throws EventCancelledException {
        PermissionEntrySetPermissionEvent permissionEntrySetPermissionEvent = new KissenPermissionEntrySetPermissionEvent(translatePermission(kissenPermissionNode, record -> {/* ignored */}), getPermission(kissenPermissionNode.name()).isPresent());

        if (KissenCore.getInstance()
                .getImplementation(EventImplementation.class)
                .call(permissionEntrySetPermissionEvent)) {
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
     * @param kissenPermissionNode The Kissen permission node for which the permission should be set. Must not be null.
     * @return A non-null value representing the result of internally setting the permission.
     */
    private @NotNull T internSetPermission(@NotNull KissenPermissionNode kissenPermissionNode) {
        SavableRecordList<KissenPermissionNode> savableRecordList =
                getListNotNull("permission_list").toRecordList(KissenPermissionNode.class);
        savableRecordList.toRecordList()
                .stream()
                .filter(permission -> permission.equals(kissenPermissionNode))
                .findFirst()
                .ifPresentOrElse(kissenPermissionNode1 ->
                        savableRecordList.replaceRecord(currentPermission -> currentPermission.name()
                                .equals(kissenPermissionNode.name()), kissenPermissionNode), () -> savableRecordList.add(kissenPermissionNode));
        return translatePermission(kissenPermissionNode, getSaveChanges());
    }

    /**
     * Translates a Kissen permission node into the appropriate permission representation.
     * <p>
     * This method is responsible for translating a given Kissen permission node into the suitable permission representation within the context
     * of the system. It may involve formatting, conversion, or other transformations required for accurate permission management.
     *
     * @param kissenPermissionNode The Kissen permission node to be translated. Must not be null.
     * @param dataWriter           An optional data writer for storing the translated permission. Can be null.
     * @return A non-null value representing the translated permission result.
     */
    protected abstract @NotNull T translatePermission(@NotNull KissenPermissionNode kissenPermissionNode, @Nullable DataWriter dataWriter);


    /**
     * Retrieves a data writer for saving changes made to permission data.
     * <p>
     * This method provides a data writer that facilitates the process of saving changes to permission data after it has been modified.
     * It returns a non-null data writer instance that can be used to store changes in the context of the system's permission management.
     *
     * @return A non-null data writer instance for saving permission changes.
     */
    private @NotNull DataWriter getSaveChanges() {
        return record -> internSetPermission((KissenPermissionNode) record);
    }

    @Override
    public int softDelete() throws BackendException
    {
        int rows = super.softDelete();
        permissionUpdate();
        return rows;
    }
}
