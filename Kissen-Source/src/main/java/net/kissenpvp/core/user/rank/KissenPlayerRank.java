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

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kissenpvp.core.api.user.rank.PlayerRank;
import net.kissenpvp.core.api.user.rank.Rank;
import net.kissenpvp.core.api.user.rank.RankImplementation;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.DataWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;


@AllArgsConstructor
public class KissenPlayerRank<T extends Rank> implements PlayerRank<T> {
    private final @Getter
    @NotNull KissenPlayerRankNode kissenPlayerRankNode;
    private final @Nullable DataWriter dataWriter;

    @Override
    public String toString() {
        return getID();
    }

    @Override
    public @NotNull String getID() {
        return kissenPlayerRankNode.id();
    }

    @Override
    public @NotNull Optional<T> getSource() {
        return (Optional<T>) KissenCore.getInstance().getImplementation(RankImplementation.class).getRank(kissenPlayerRankNode.rankID());
    }

    @Override
    public long getStart() {
        return kissenPlayerRankNode.start();
    }

    @Override
    public @NotNull Optional<@Nullable Duration> getDuration() {
        if (kissenPlayerRankNode.duration().getValue() == null) {
            return Optional.empty();
        }
        return Optional.of(Duration.of(kissenPlayerRankNode.duration().getValue(), ChronoUnit.MILLIS));
    }

    @Override
    public long getEnd() {
        return kissenPlayerRankNode.end().getValue();
    }

    @Override
    public void setEnd(long end) {
        if (dataWriter == null) {
            throw new UnsupportedOperationException("This object is unmodifiable.");
        }
        kissenPlayerRankNode.end().setValue(end);
        dataWriter.update(kissenPlayerRankNode);
    }

    @Override
    public boolean isValid() {
        return getEnd() > System.currentTimeMillis() || getEnd() == -1;
    }

    @Override
    public long getPredictedEnd() {
        return kissenPlayerRankNode.predictedEnd();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KissenPlayerRank<?> that)) {
            return false;
        }
        return kissenPlayerRankNode.id().equals(that.kissenPlayerRankNode.id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(kissenPlayerRankNode.rankID());
    }
}
