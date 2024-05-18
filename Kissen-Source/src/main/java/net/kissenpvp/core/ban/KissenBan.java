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

package net.kissenpvp.core.ban;

import net.kissenpvp.core.api.ban.AbstractBan;
import net.kissenpvp.core.api.ban.BanType;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.Meta;
import net.kissenpvp.core.api.database.meta.Table;
import net.kissenpvp.core.api.database.savable.SavableMap;
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.networking.socket.DataPackage;
import net.kissenpvp.core.api.time.AccurateDuration;
import net.kissenpvp.core.ban.events.ban.BanAlterDurationEvent;
import net.kissenpvp.core.ban.events.ban.BanAlterTypeEvent;
import net.kissenpvp.core.ban.events.ban.BanRenameEvent;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.savable.KissenSavable;
import net.kissenpvp.core.database.savable.KissenSavableMap;
import net.kissenpvp.core.database.savable.SerializableSavableHandler;
import net.kissenpvp.core.event.EventImplementation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public abstract class KissenBan extends KissenSavable<Integer> implements AbstractBan {

    @Override
    public int getID() {
        return getRawID();
    }

    @Override
    public @NotNull String getSaveID() {
        return "banid";
    }

    @Override
    public @NotNull Table getTable() {
        return KissenCore.getInstance().getImplementation(KissenBanImplementation.class).getTable();
    }

    @Override
    protected @NotNull SavableMap createRepository(@Nullable Map<String, Object> data) {
        Meta meta = KissenCore.getInstance().getImplementation(KissenBanImplementation.class).getMeta();
        return new KissenSavableMap(getDatabaseID(), meta, Objects.requireNonNullElseGet(data, meta.getData(getDatabaseID())::join));
    }

    @Override
    public String[] getKeys() {
        return new String[]{"name", "ban_type"};
    }

    @Override
    public @NotNull String getName() {
        return getRepository().getNotNull("name", String.class);
    }

    @Override
    public void setName(@NotNull String name) throws EventCancelledException {

        BanRenameEvent<?> banRenameEvent = new BanRenameEvent<>(this, getName(), name);
        if (!KissenCore.getInstance().getImplementation(EventImplementation.class).call(banRenameEvent)) {
            throw new EventCancelledException(banRenameEvent);
        }

        getRepository().set("name", banRenameEvent.getUpdateName());
    }

    @Override
    public @NotNull BanType getBanType() {
        return getRepository().getNotNull("ban_type", BanType.class);
    }

    @Override
    public void setBanType(@NotNull BanType banType) throws EventCancelledException {

        BanAlterTypeEvent<?> banAlterTypeEvent = new BanAlterTypeEvent<>(this, getBanType(), banType);
        if (!KissenCore.getInstance().getImplementation(EventImplementation.class).call(banAlterTypeEvent)) {
            throw new EventCancelledException(banAlterTypeEvent);
        }

        getRepository().set("ban_type", banType);
    }

    @Override
    public @NotNull Optional<AccurateDuration> getDuration() {
        return getRepository().get("duration", AccurateDuration.class);
    }

    @Override
    public void setDuration(@Nullable AccurateDuration duration) throws EventCancelledException {

        BanAlterDurationEvent<?> banAlterDurationEvent = new BanAlterDurationEvent<>(this, duration);
        if (!KissenCore.getInstance().getImplementation(EventImplementation.class).call(banAlterDurationEvent)) {
            throw new EventCancelledException(banAlterDurationEvent);
        }

        getRepository().set("duration", duration);
    }

    @Override
    public int softDelete() throws BackendException {
        KissenCore.getInstance().getImplementation(KissenBanImplementation.class).remove(this);
        return super.softDelete();
    }

    @Override
    public void sendData(@NotNull DataPackage dataPackage) {

    }

    @Override
    public SerializableSavableHandler getSerializableSavableHandler() {
        return null;
    }
}
