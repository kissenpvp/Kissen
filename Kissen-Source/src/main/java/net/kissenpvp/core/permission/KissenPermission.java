/*
 * Copyright 2023 KissenPvP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;


public class KissenPermission implements Permission {
    protected final KissenPermissionNode kissenPermissionNode;
    protected final PermissionEntry<? extends Permission> permissionEntry;
    protected final DataWriter dataWriter;

    public KissenPermission(@NotNull KissenPermissionNode kissenPermissionNode, @NotNull PermissionEntry<? extends Permission> permissionEntry, @Nullable DataWriter dataWriter) {
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
            throw new UnsupportedOperationException("This object is immutable.");
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
    public long getStart() {
        return getKissenPermissionNode().start();
    }

    @Override
    public @Nullable Duration getDuration() {
        return getKissenPermissionNode().duration().getValue() == null ? null : Duration.of(getKissenPermissionNode().duration().getValue(), ChronoUnit.MILLIS);
    }

    @Override
    public long getEnd() {
        return getKissenPermissionNode().end().getValue();
    }

    @Override
    public void setEnd(long end) throws EventCancelledException {
        if (dataWriter == null) {
            throw new UnsupportedOperationException("This object is immutable.");
        }
        PermissionEndUpdateEvent permissionEndUpdateEvent = new KissenPermissionEndUpdateEvent(this, end);
        if (KissenCore.getInstance().getImplementation(EventImplementation.class).call(permissionEndUpdateEvent)) {
            kissenPermissionNode.end().setValue(permissionEndUpdateEvent.getEnd().orElse(-1L));
            dataWriter.update(kissenPermissionNode);
            return;
        }
        throw new EventCancelledException();
    }

    @Override
    public long getPredictedEnd() {
        return kissenPermissionNode.predictedEnd();
    }

    @Override
    public boolean isValid() {
        return (getEnd() > System.currentTimeMillis() || getEnd() == -1);
    }

    @Override
    public void setOption(@NotNull String key, @NotNull String data) throws EventCancelledException {
        if (dataWriter == null) {
            throw new UnsupportedOperationException("This object is immutable.");
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
            throw new UnsupportedOperationException("This object is immutable.");
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

    public @NotNull KissenPermissionNode getKissenPermissionNode() {
        return kissenPermissionNode;
    }

    public @Nullable DataWriter getDataWriter() {
        return dataWriter;
    }
}
