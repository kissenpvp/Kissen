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

import net.kissenpvp.core.api.message.ColorProvider;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

public class KissenColorProvider extends ColorProvider {
    public KissenColorProvider() {
        instance = this;
    }

    @Override
    public @NotNull TextColor getPrimaryColor() {
        return EnumColorProvider.PRIMARY.getTextColor();
    }

    @Override
    public @NotNull TextColor getSecondaryColor() {
        return EnumColorProvider.SECONDARY.getTextColor();
    }

    @Override
    public @NotNull TextColor getDefaultColor() {
        return EnumColorProvider.DEFAULT.getTextColor();
    }

    @Override
    public @NotNull TextColor getEnabledColor() {
        return EnumColorProvider.ENABLED.getTextColor();
    }

    @Override
    public @NotNull TextColor getDisabledColor() {
        return EnumColorProvider.DISABLED.getTextColor();
    }

    public @NotNull Optional<@Nullable EnumColorProvider> getColor(int value) {
        return Arrays.stream(EnumColorProvider.values())
                .filter(enumColorProvider -> enumColorProvider.getTextColor().value() == value)
                .findFirst();
    }
}
