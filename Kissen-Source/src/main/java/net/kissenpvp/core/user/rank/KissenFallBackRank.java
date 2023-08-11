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

import net.kissenpvp.core.api.user.rank.Rank;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public class KissenFallBackRank implements Rank
{
    @Override public @NotNull String getName()
    {
        return "Player";
    }

    @Override public int getPriority()
    {
        return 999999999;
    }

    @Override public void setPriority(int priority)
    {
        throw new UnsupportedOperationException();
    }

    @Override public @NotNull Optional<@Nullable Component> getPrefix()
    {
        return Optional.of(Component.text("Player |").color(getChatColor()));
    }

    @Override public void setPrefix(@Nullable Component prefix)
    {
        throw new UnsupportedOperationException();
    }

    @Override public @NotNull NamedTextColor getChatColor()
    {
        return NamedTextColor.GRAY;
    }

    @Override public void setChatColor(@NotNull NamedTextColor chatColor)
    {
        throw new UnsupportedOperationException();
    }

    @Override public @NotNull Optional<Component> getSuffix()
    {
        return Optional.empty();
    }

    @Override public void setSuffix(@Nullable Component suffix)
    {
        throw new UnsupportedOperationException();
    }

    @Override public int delete()
    {
        throw new UnsupportedOperationException();
    }
}
