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

import net.kissenpvp.core.api.ban.Ban;
import net.kissenpvp.core.api.ban.BanType;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.time.AccurateDuration;
import net.kissenpvp.core.api.time.TimeImplementation;
import net.kissenpvp.core.ban.events.ban.BanAlterDurationEvent;
import net.kissenpvp.core.ban.events.ban.BanAlterTypeEvent;
import net.kissenpvp.core.ban.events.ban.BanRenameEvent;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.savable.KissenSavable;
import net.kissenpvp.core.database.savable.SerializableSavableHandler;
import net.kissenpvp.core.event.EventImplementation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class KissenBan extends KissenSavable implements Ban {

    @Override
    public int getID() {
        return Integer.parseInt(getRawID());
    }

    @Override
    public @NotNull String getSaveID() {
        return "banid";
    }

    @Override
    public String[] getKeys() {
        return new String[]{"name", "ban_type"};
    }

    @Override
    public @NotNull String getName() {
        return getNotNull("name", String.class);
    }

    @Override
    public void setName(@NotNull String name) throws EventCancelledException {

        BanRenameEvent<?> banRenameEvent = new BanRenameEvent<>(this, getName(), name);
        if(!KissenCore.getInstance().getImplementation(EventImplementation.class).call(banRenameEvent))
        {
            throw new EventCancelledException(banRenameEvent);
        }

        set("name", banRenameEvent.getUpdateName());
    }

    @Override
    public @NotNull BanType getBanType() {
        return BanType.valueOf(getNotNull("ban_type", String.class));
    }

    @Override
    public void setBanType(@NotNull BanType banType) throws EventCancelledException {

        BanAlterTypeEvent<?> banAlterTypeEvent = new BanAlterTypeEvent<>(this, getBanType(), banType);
        if(!KissenCore.getInstance().getImplementation(EventImplementation.class).call(banAlterTypeEvent))
        {
            throw new EventCancelledException(banAlterTypeEvent);
        }

        set("ban_type", banType.name());
    }

    @Override
    public @NotNull Optional<AccurateDuration> getAccurateDuration() {
        return get("duration", Long.class).map(val -> KissenCore.getInstance().getImplementation(TimeImplementation.class).millis(val));
    }

    @Override
    public void setAccurateDuration(@Nullable AccurateDuration duration) throws EventCancelledException {

        BanAlterDurationEvent<?> banAlterDurationEvent = new BanAlterDurationEvent<>(this, duration);
        if(!KissenCore.getInstance().getImplementation(EventImplementation.class).call(banAlterDurationEvent))
        {
            throw new EventCancelledException(banAlterDurationEvent);
        }

        set("duration", Optional.ofNullable(duration)
                .map(AccurateDuration::getMillis)
                .map(String::valueOf)
                .orElse(null));
    }

    @Override
    public int softDelete() throws BackendException {
        KissenCore.getInstance().getImplementation(KissenBanImplementation.class).remove(this);
        return super.softDelete();
    }

    @Override
    public SerializableSavableHandler getSerializableSavableHandler() {
        return null;
    }
}
