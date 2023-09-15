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
    public @NotNull Optional<Duration> getDuration() {
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
