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

import lombok.Getter;
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.time.KissenTemporalObject;
import net.kissenpvp.core.api.user.rank.AbstractPlayerRank;
import net.kissenpvp.core.api.user.rank.AbstractRank;
import net.kissenpvp.core.api.user.rank.AbstractRankImplementation;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.api.database.DataWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;


public class KissenPlayerRank<T extends AbstractRank> extends KissenTemporalObject implements AbstractPlayerRank<T> {

    private final @Getter
    @NotNull PlayerRankNode playerRankNode;
    private final @Nullable DataWriter<PlayerRankNode> dataWriter;

    public KissenPlayerRank(@NotNull PlayerRankNode playerRankNode, @Nullable DataWriter<PlayerRankNode> dataWriter) {
        super(playerRankNode.temporalData());
        this.playerRankNode = playerRankNode;
        this.dataWriter = dataWriter;
    }

    @Override
    public String toString()
    {
        return "KissenPlayerRank{" + getSource() + "}";
    }

    @Override
    public @NotNull String getID() {
        return playerRankNode.id();
    }

    @Override
    public @NotNull T getSource()
    {
        AbstractRankImplementation<T> rankImplementation = KissenCore.getInstance().getImplementation(AbstractRankImplementation.class);
        return rankImplementation.getRank(playerRankNode.rankID()).orElseThrow(NullPointerException::new);
    }

    @Override
    public void setEnd(@Nullable Instant end) throws EventCancelledException {
        DataWriter.validate(dataWriter, new EventCancelledException());

        rewriteEnd(end);
        dataWriter.update(playerRankNode);
    }

    @Override
    public boolean isValid()
    {
        try
        {
            getSource();
            return super.isValid();
        }
        catch (NullPointerException ignored) {}
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KissenPlayerRank<?> that)) {
            return false;
        }
        return playerRankNode.id().equals(that.playerRankNode.id());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getPlayerRankNode(), getSource().hashCode());
    }
}
