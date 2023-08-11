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

package net.kissenpvp.core.api.config.options;

import net.kissenpvp.core.api.config.AbstractOption;
import net.kissenpvp.core.api.config.InvalidValueException;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public abstract class OptionNamedTextColor extends AbstractOption<NamedTextColor> {
    @Override
    public @NotNull String serialize(@NotNull NamedTextColor value) {
        return value.toString();
    }

    @Override
    public @NotNull NamedTextColor deserialize(@NotNull String input) throws InvalidValueException {
        NamedTextColor namedTextColor = NamedTextColor.NAMES.value(input);
        if (namedTextColor == null) {
            throw new InvalidValueException(input, getCode());
        }
        return namedTextColor;
    }
}
