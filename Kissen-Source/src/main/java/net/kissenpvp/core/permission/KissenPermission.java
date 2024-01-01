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

import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.event.EventImplementation;
import net.kissenpvp.core.api.permission.Permission;
import net.kissenpvp.core.api.permission.PermissionEntry;
import net.kissenpvp.core.api.permission.event.PermissionEndUpdateEvent;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.DataWriter;
import net.kissenpvp.core.permission.event.KissenPermissionEndUpdateEvent;
import net.kissenpvp.core.permission.event.KissenPermissionOptionDeleteEvent;
import net.kissenpvp.core.permission.event.KissenPermissionOptionSetEvent;
import net.kissenpvp.core.permission.event.KissenPermissionValueUpdateEvent;
import net.kissenpvp.core.time.KissenTemporalObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;


public class KissenPermission extends KissenTemporalObject implements Permission {

    protected final KissenPermissionNode kissenPermissionNode;
    protected final PermissionEntry<? extends Permission> permissionEntry;
    protected final DataWriter dataWriter;

    public KissenPermission(@NotNull KissenPermissionNode kissenPermissionNode, @NotNull PermissionEntry<? extends Permission> permissionEntry, @Nullable DataWriter dataWriter) {
        super(kissenPermissionNode.temporalMeasureNode());
        this.kissenPermissionNode = kissenPermissionNode;
        this.permissionEntry = permissionEntry;
        this.dataWriter = dataWriter;
    }

    @Override
    public @NotNull String getName() {
        return kissenPermissionNode.name();
    }

    @Override
    public @NotNull PermissionEntry<? extends Permission> getOwner() {
        return permissionEntry;
    }

    @Override
    public boolean getValue() {
        return kissenPermissionNode.value().getValue();
    }

    @Override
    public void setValue(boolean value) throws EventCancelledException {
        if (dataWriter == null) {
            throw new EventCancelledException("This object is immutable.", new IllegalStateException());
        }

        KissenPermissionValueUpdateEvent kissenPermissionValueUpdateEvent = new KissenPermissionValueUpdateEvent(this, value);
        if (KissenCore.getInstance().getImplementation(EventImplementation.class).call(kissenPermissionValueUpdateEvent) && getValue() != kissenPermissionValueUpdateEvent.isValue()) {
            kissenPermissionNode.value().setValue(kissenPermissionValueUpdateEvent.isValue());
            dataWriter.update(kissenPermissionNode);
            return;
        }
        throw new EventCancelledException();
    }

    @Override
    public void setOption(@NotNull String key, @NotNull String data) throws EventCancelledException {
        if (dataWriter == null) {
            throw new EventCancelledException("This object is immutable.", new IllegalStateException());
        }
        KissenPermissionOptionSetEvent kissenPermissionOptionSetEvent = new KissenPermissionOptionSetEvent(this, kissenPermissionNode.additionalData().containsKey(key), key, data);
        if (KissenCore.getInstance().getImplementation(EventImplementation.class).call(kissenPermissionOptionSetEvent)) {
            kissenPermissionNode.additionalData().remove(kissenPermissionOptionSetEvent.getKey());
            kissenPermissionNode.additionalData().put(kissenPermissionOptionSetEvent.getKey(), kissenPermissionOptionSetEvent.getData());
            dataWriter.update(kissenPermissionNode);
            return;
        }
        throw new EventCancelledException();
    }

    @Override
    public boolean deleteOption(@NotNull String key) throws EventCancelledException {
        if (dataWriter == null) {
            throw new EventCancelledException("This object is immutable.", new IllegalStateException());
        }

        if (kissenPermissionNode.additionalData().containsKey(key)) {
            KissenPermissionOptionDeleteEvent kissenPermissionOptionDeleteEvent = new KissenPermissionOptionDeleteEvent(this, key, Objects.requireNonNull(getOption(key)));
            if (KissenCore.getInstance().getImplementation(EventImplementation.class).call(kissenPermissionOptionDeleteEvent)) {
                kissenPermissionNode.additionalData().remove(key);
                dataWriter.update(kissenPermissionNode);
                return true;
            }
            throw new EventCancelledException();
        }

        return false;
    }

    @Override
    public @Nullable String getOption(@NotNull String key) {
        return kissenPermissionNode.additionalData().get(key);
    }

    @Override
    public @Unmodifiable @NotNull Map<String, String> getDefinedOptions() {
        return Collections.unmodifiableMap(kissenPermissionNode.additionalData());
    }

    @Override
    public void setEnd(Instant end) throws EventCancelledException {
        if (dataWriter == null) {
            throw new EventCancelledException("This object is immutable.", new IllegalStateException());
        }

        PermissionEndUpdateEvent permissionEndUpdateEvent = new KissenPermissionEndUpdateEvent(this, end);
        if (KissenCore.getInstance().getImplementation(EventImplementation.class).call(permissionEndUpdateEvent)) {
            rewriteEnd(permissionEndUpdateEvent.getEnd().orElse(null));
            dataWriter.update(kissenPermissionNode);
            return;
        }
        throw new EventCancelledException();
    }

    @Override
    public boolean isValid()
    {
        return getValue() && super.isValid();
    }

    public @NotNull KissenPermissionNode getKissenPermissionNode() {
        return kissenPermissionNode;
    }

    public @Nullable DataWriter getDataWriter() {
        return dataWriter;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KissenPermission that = (KissenPermission) o;
        return Objects.equals(getKissenPermissionNode().name(),
                that.getKissenPermissionNode().name()) && Objects.equals(permissionEntry, that.permissionEntry);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getKissenPermissionNode().name(), permissionEntry);
    }
}
