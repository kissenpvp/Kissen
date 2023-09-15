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

package net.kissenpvp.core.message.usersettings;

import net.kissenpvp.core.api.user.usersetttings.PlayerSetting;
import net.kissenpvp.core.api.user.usersetttings.UserValue;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public abstract class UserColorSetting implements PlayerSetting<NamedTextColor> {
    @Override
    public @NotNull String serialize(@NotNull NamedTextColor value) {
        return value.toString();
    }

    @Override
    public @NotNull NamedTextColor deserialize(@NotNull String input) throws IllegalArgumentException {
        NamedTextColor namedTextColor = NamedTextColor.NAMES.value(input);
        if (namedTextColor == null) {
            throw new IllegalArgumentException();
        }
        return namedTextColor;
    }

    @Override
    public @NotNull UserValue<NamedTextColor>[] getPossibleValues() {
        return new UserValue[]
                {
                        new UserValue<>(NamedTextColor.DARK_BLUE),
                        new UserValue<>(NamedTextColor.DARK_GRAY),
                        new UserValue<>(NamedTextColor.DARK_AQUA),
                        new UserValue<>(NamedTextColor.DARK_RED),
                        new UserValue<>(NamedTextColor.DARK_PURPLE),
                        new UserValue<>(NamedTextColor.GOLD),
                        new UserValue<>(NamedTextColor.BLUE),
                        new UserValue<>(NamedTextColor.GREEN),
                        new UserValue<>(NamedTextColor.AQUA),
                        new UserValue<>(NamedTextColor.RED),
                        new UserValue<>(NamedTextColor.LIGHT_PURPLE),
                        new UserValue<>(NamedTextColor.YELLOW),
                        new UserValue<>(NamedTextColor.WHITE),
                };
    }
}
