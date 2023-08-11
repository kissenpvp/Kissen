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

import lombok.Getter;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public enum EnumColorProvider {

    PRIMARY(TextColor.color(112114105)),
    SECONDARY(TextColor.color(11510199)),
    DEFAULT(TextColor.color(100101102)),
    ENABLED(TextColor.color(10111097)),
    DISABLED(TextColor.color(100105115));

    @Getter
    private final TextColor textColor;

    EnumColorProvider(@NotNull TextColor textColor) {
        this.textColor = textColor;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
