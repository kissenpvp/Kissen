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

package net.kissenpvp.core.message.serializer;

import net.kissenpvp.core.api.base.serializer.TextSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class KissenLegacySerializer implements TextSerializer<Component> {
    @Override
    public @NotNull String serialize(@NotNull Component object) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(object).replace('&', '§');
    }

    @Override
    public @NotNull Component deserialize(@NotNull String input) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(input);
    }
}
