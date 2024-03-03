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

package net.kissenpvp.core.user.suffix;

import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.user.suffix.Suffix;
import net.kissenpvp.core.database.DataWriter;
import net.kissenpvp.core.time.KissenTemporalObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;

public class KissenSuffix extends KissenTemporalObject implements Suffix
{
    private final SuffixNode suffixNode;
    private final DataWriter<SuffixNode> dataWriter;

    public KissenSuffix(@NotNull SuffixNode suffixNode, @Nullable DataWriter<SuffixNode> dataWriter)
    {
        super(suffixNode.temporalMeasure());
        this.suffixNode = suffixNode;
        this.dataWriter = dataWriter;
    }

    @Override public @NotNull String getName()
    {
        return suffixNode.name();
    }

    @Override public @NotNull Component getContent()
    {
        return suffixNode.content();
    }

    @Override
    public boolean equals(Object o) {
        if (this==o) return true;
        if (o==null || getClass()!=o.getClass()) return false;
        KissenSuffix that = (KissenSuffix) o;
        return Objects.equals(suffixNode, that.suffixNode);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(suffixNode);
    }

    @Override
    public void setEnd(@Nullable Instant end) throws EventCancelledException
    {
        if(dataWriter == null)
        {
            throw new EventCancelledException();
        }

        rewriteEnd(end);
        dataWriter.update(suffixNode);
    }
}
