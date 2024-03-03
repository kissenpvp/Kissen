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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.kissenpvp.core.time.TemporalMeasureNode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public record SuffixNode(@NotNull String name, @NotNull JsonElement content,
                         @NotNull TemporalMeasureNode temporalMeasureNode) {

    public SuffixNode(@NotNull String name, @NotNull Component content, @NotNull TemporalMeasureNode temporalMeasureNode) {
        this(name, JsonParser.parseString(JSONComponentSerializer.json().serialize(content)), temporalMeasureNode);
    }

    public SuffixNode(@NotNull String name, @NotNull Component content) {
        this(name, content, new TemporalMeasureNode());
    }

    @Override
    public boolean equals(Object o) {
        if (this==o) return true;
        if (o==null || getClass()!=o.getClass()) return false;
        SuffixNode that = (SuffixNode) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
