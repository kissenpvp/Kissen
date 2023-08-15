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

package net.kissenpvp.core.user.rank;

import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.api.message.ComponentSerializer;
import net.kissenpvp.core.api.user.rank.Rank;
import net.kissenpvp.core.api.user.rank.RankImplementation;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.savable.KissenSavable;
import net.kissenpvp.core.database.savable.SerializableSavableHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        return Integer.parseInt(getNotNull("priority"));
    }

    @Override
    public void setPriority(int priority) {
        set("priority", String.valueOf(priority));
    }

    @Override
    public @NotNull Optional<Component> getPrefix() {
        if (containsKey("prefix")) {
            return Optional.of(ComponentSerializer.getInstance().getJsonSerializer().deserialize(getNotNull("prefix")));
        }
        return Optional.empty();
    }

    @Override
    public void setPrefix(@Nullable Component prefix) {
        if (prefix == null) {
            delete("prefix");
            return;
        }
        set("prefix", ComponentSerializer.getInstance().getJsonSerializer().serialize(prefix));
    }

    @Override
    public @NotNull NamedTextColor getChatColor() {
        return NamedTextColor.NAMES.valueOr(getNotNull("chat_color"), NamedTextColor.WHITE);
    }

    @Override
    public void setChatColor(@NotNull NamedTextColor chatColor) {
        set("chat_color", chatColor.toString());
    }

    @Override
    public @NotNull Optional<Component> getSuffix() {
        if (containsKey("suffix")) {
            return Optional.of(ComponentSerializer.getInstance().getJsonSerializer().deserialize(getNotNull("suffix")));
        }
        return Optional.empty();
    }

    @Override
    public void setSuffix(@Nullable Component suffix) {
        if (suffix == null) {
            delete("suffix");
            return;
        }
        set("suffix", ComponentSerializer.getInstance().getJsonSerializer().serialize(suffix));
    }

    @Override
    public SerializableSavableHandler getSerializableSavableHandler() {
        return new SerializableRankHandler(this.getName());
    }

    @Override
    public int softDelete() throws BackendException {
        ((KissenRankImplementation<?>) KissenCore.getInstance().getImplementation(RankImplementation.class)).removeRank(this);
        return super.softDelete();
    }

}