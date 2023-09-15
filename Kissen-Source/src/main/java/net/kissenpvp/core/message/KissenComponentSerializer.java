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

package net.kissenpvp.core.message;

import net.kissenpvp.core.api.base.serializer.TextSerializer;
import net.kissenpvp.core.api.message.ComponentSerializer;
import net.kissenpvp.core.message.serializer.KissenJsonSerializer;
import net.kissenpvp.core.message.serializer.KissenLegacySerializer;
import net.kissenpvp.core.message.serializer.KissenMiniSerializer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class KissenComponentSerializer extends ComponentSerializer {
    private final TextSerializer<Component> miniSerializer, jsonSerializer, legacySerializer;

    public KissenComponentSerializer() {
        instance = this;
        this.miniSerializer = new KissenMiniSerializer();
        this.jsonSerializer = new KissenJsonSerializer();
        this.legacySerializer = new KissenLegacySerializer();
    }

    @Override
    public @NotNull TextSerializer<Component> getMiniSerializer() {
        return miniSerializer;
    }

    @Override
    public @NotNull TextSerializer<Component> getJsonSerializer() {
        return jsonSerializer;
    }

    @Override
    public @NotNull TextSerializer<Component> getLegacySerializer() {
        return legacySerializer;
    }
}
