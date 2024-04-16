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

import net.kissenpvp.core.api.database.DataWriter;
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.permission.AbstractPermission;
import net.kissenpvp.core.api.permission.AbstractPermissionEntry;
import net.kissenpvp.core.api.time.KissenTemporalObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;


public class KissenPermission extends KissenTemporalObject implements AbstractPermission {

    protected final PermissionNode permissionNode;
    protected final AbstractPermissionEntry<? extends AbstractPermission> permissionEntry;
    protected final DataWriter<PermissionNode> dataWriter;

    public KissenPermission(@NotNull PermissionNode permissionNode, @NotNull AbstractPermissionEntry<? extends AbstractPermission> permissionEntry, @Nullable DataWriter<PermissionNode> dataWriter) {
        super(permissionNode.temporalData());
        this.permissionNode = permissionNode;
        this.permissionEntry = permissionEntry;
        this.dataWriter = dataWriter;
    }

    @Override
    public @NotNull String getName() {
        return permissionNode.name();
    }

    @Override
    public @NotNull AbstractPermissionEntry<? extends AbstractPermission> getOwner() {
        return permissionEntry;
    }

    @Override
    public boolean getValue() {
        return Boolean.TRUE.equals(permissionNode.value().getValue());
    }

    @Override
    public void setValue(boolean value) throws EventCancelledException {
        if (dataWriter==null) {
            throw new EventCancelledException("This object is immutable.", new IllegalStateException());
        }

        permissionNode.value().setValue(value);
        dataWriter.update(permissionNode);
    }

    @Override
    public void setOption(@NotNull String key, @NotNull String data) throws EventCancelledException {
        if (dataWriter==null) {
            throw new EventCancelledException("This object is immutable.", new IllegalStateException());
        }
        permissionNode.additionalData().remove(key);
        permissionNode.additionalData().put(key, data);
        dataWriter.update(permissionNode);
    }

    @Override
    public boolean deleteOption(@NotNull String key) throws EventCancelledException {
        if (dataWriter==null) {
            throw new EventCancelledException("This object is immutable.", new IllegalStateException());
        }

        if (permissionNode.additionalData().containsKey(key)) {
            permissionNode.additionalData().remove(key);
            dataWriter.update(permissionNode);
            return true;
        }

        return false;
    }

    @Override
    public @Nullable String getOption(@NotNull String key) {
        return permissionNode.additionalData().get(key);
    }

    @Override
    public @Unmodifiable @NotNull Map<String, String> getDefinedOptions() {
        return Collections.unmodifiableMap(permissionNode.additionalData());
    }

    @Override
    public void setEnd(@Nullable Instant end) throws EventCancelledException {
        if (dataWriter==null) {
            throw new EventCancelledException("This object is immutable.", new IllegalStateException());
        }

        rewriteEnd(end);
        dataWriter.update(permissionNode);
    }

    @Override
    public boolean isValid() {
        return getValue() && super.isValid();
    }

    public @NotNull PermissionNode getKissenPermissionNode() {
        return permissionNode;
    }

    public @Nullable DataWriter<PermissionNode> getDataWriter() {
        return dataWriter;
    }

    @Override
    public boolean equals(Object o) {
        if (this==o) return true;
        if (o==null || getClass()!=o.getClass()) return false;
        KissenPermission that = (KissenPermission) o;
        return Objects.equals(getKissenPermissionNode().name(), that.getKissenPermissionNode().name()) && Objects.equals(permissionEntry, that.permissionEntry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKissenPermissionNode().name(), permissionEntry);
    }
}
