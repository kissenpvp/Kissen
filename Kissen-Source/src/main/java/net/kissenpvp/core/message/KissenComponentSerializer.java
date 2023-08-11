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
