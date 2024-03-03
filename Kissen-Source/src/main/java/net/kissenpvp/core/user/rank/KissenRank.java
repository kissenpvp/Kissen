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

package net.kissenpvp.core.user.rank;

import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.api.user.rank.Rank;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.savable.KissenSavable;
import net.kissenpvp.core.database.savable.SerializableSavableHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;


public abstract class KissenRank extends KissenSavable implements Savable, Rank {
    @Override
    public final @NotNull String getSaveID() {
        return "rank";
    }

    @Override
    public String[] getKeys() {
        return new String[]{"priority", "chat_color"};
    }

    @Override
    public @NotNull String getName() {
        return getRawID();
    }


    @Override
    public int getPriority() {
        return getNotNull("priority", Integer.class);
    }

    @Override
    public void setPriority(int priority) {
        set("priority", priority);
    }

    @Override
    public @NotNull Optional<Component> getPrefix() {
        return get("prefix", Component.class);
    }

    @Override
    public void setPrefix(@Nullable Component prefix) {
        if (prefix == null) {
            delete("prefix");
            return;
        }
        set("prefix", prefix);
    }

    @Override
    public @NotNull TextColor getChatColor() {
        return getNotNull("chat_color", TextColor.class);
    }

    @Override
    public void setChatColor(@NotNull TextColor chatColor) {
        set("chat_color", chatColor);
    }

    @Override
    public @NotNull Optional<Component> getSuffix() {
        return get("suffix", Component.class);
    }

    @Override
    public void setSuffix(@Nullable Component suffix) {
        if (suffix == null) {
            delete("suffix");
            return;
        }
        set("suffix", suffix);
    }

    @Override
    public SerializableSavableHandler getSerializableSavableHandler() {
        return new SerializableRankHandler(this.getName());
    }

    @Override
    public int softDelete() throws BackendException {
        KissenCore.getInstance().getImplementation(KissenRankImplementation.class).removeRank(this);
        return super.softDelete();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDatabaseID(), getPriority(), getPrefix(), getChatColor().asHexString(), getSuffix());
    }
}
