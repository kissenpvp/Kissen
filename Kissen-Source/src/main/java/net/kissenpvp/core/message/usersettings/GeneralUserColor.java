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

import net.kissenpvp.core.api.config.ConfigurationImplementation;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.user.usersetttings.UserValue;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.message.settings.DefaultColor;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public class GeneralUserColor extends UserColorSetting
{
    @Override public @NotNull String getKey()
    {
        return "generalcolor";
    }

    @Override
    public @NotNull NamedTextColor getDefaultValue(@NotNull PlayerClient<?, ?, ?> playerClient)
    {
        return KissenCore.getInstance().getImplementation(ConfigurationImplementation.class).getSetting(DefaultColor.class);
    }

    @Override
    public @NotNull UserValue<NamedTextColor>[] getPossibleValues(@NotNull PlayerClient<?, ?, ?> playerClient) {
        return new UserValue[]
                {
                        new UserValue<>(NamedTextColor.GRAY),
                        new UserValue<>(NamedTextColor.WHITE),
                        new UserValue<>(NamedTextColor.DARK_GRAY)
                };
    }
}
