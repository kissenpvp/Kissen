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

    public @NotNull Optional<EnumColorProvider> getColor(int value) {
        return Arrays.stream(EnumColorProvider.values())
                .filter(enumColorProvider -> enumColorProvider.getTextColor().value() == value)
                .findFirst();
    }
}
