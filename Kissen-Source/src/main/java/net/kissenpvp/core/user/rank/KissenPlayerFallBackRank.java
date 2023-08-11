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

import net.kissenpvp.core.api.user.rank.PlayerRank;
import net.kissenpvp.core.api.user.rank.Rank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Optional;


public abstract class KissenPlayerFallBackRank<T extends Rank> implements PlayerRank<T>
{

    @Override public @NotNull String getID()
    {
        return "undefined";
    }

    @Override public String toString()
    {
        return getID();
    }

    @Override public long getStart()
    {
        throw new UnsupportedOperationException();
    }

    @Override public @NotNull Optional<@Nullable Duration> getDuration()
    {
        throw new UnsupportedOperationException();
    }

    @Override public long getEnd()
    {
        throw new UnsupportedOperationException();
    }

    @Override public void setEnd(long end)
    {
        throw new UnsupportedOperationException();
    }

    @Override public boolean isValid()
    {
        return true;
    }

    @Override public long getPredictedEnd()
    {
        throw new UnsupportedOperationException();
    }
}
