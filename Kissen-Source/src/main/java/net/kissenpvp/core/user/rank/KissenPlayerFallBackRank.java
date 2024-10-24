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

import net.kissenpvp.core.api.time.AccurateDuration;
import net.kissenpvp.core.api.user.rank.AbstractPlayerRank;
import net.kissenpvp.core.api.user.rank.AbstractRank;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


public abstract class KissenPlayerFallBackRank<T extends AbstractRank> implements AbstractPlayerRank<T>
{

    @Override public @NotNull String getID()
    {
        return "undefined";
    }

    @Override public String toString()
    {
        return getID();
    }

    @Override public @NotNull Instant getStart()
    {
        throw new UnsupportedOperationException();
    }

    @Override public @NotNull Optional<AccurateDuration> getAccurateDuration()
    {
        throw new UnsupportedOperationException();
    }

    @Override public @NotNull Optional<Instant> getEnd()
    {
        throw new UnsupportedOperationException();
    }

    @Override public void setEnd(Instant end)
    {
        throw new UnsupportedOperationException();
    }

    @Override public boolean isValid()
    {
        return true;
    }

    @Override public @NotNull Optional<Instant> getPredictedEnd()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Component endComponent(@NotNull DateTimeFormatter formatter)
    {
        return Component.translatable("server.ban.punishment.end.never");
    }

    @Override
    public int hashCode()
    {
        return 0;
    }
}
