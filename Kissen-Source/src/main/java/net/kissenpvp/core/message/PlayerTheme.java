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

import lombok.AllArgsConstructor;
import net.kissenpvp.core.api.message.playersettings.*;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@AllArgsConstructor
public class PlayerTheme extends DefaultTheme {

    private final @NotNull PlayerClient<?, ?, ?> playerClient;

    @Override
    public @NotNull TextColor getPrimaryAccentColor() {
        return playerClient.getUserSetting(PrimaryUserColor.class).getValue();
    }


    @Override
    public @NotNull TextColor getSecondaryAccentColor() {
        return playerClient.getUserSetting(SecondaryUserColor.class).getValue();
    }

    @Override
    public @NotNull TextColor getGeneralColor() {
        return playerClient.getUserSetting(GeneralUserColor.class).getValue();
    }

    @Override
    public @NotNull TextColor getEnabledColor() {
        return playerClient.getUserSetting(EnabledUserColor.class).getValue();
    }

    @Override
    public @NotNull TextColor getDisabledColor() {
        return playerClient.getUserSetting(DisabledUserColor.class).getValue();
    }

    @Override
    protected @NotNull TextColor highlightColor()
    {
        if (playerClient.getUserSetting(HighlightVariables.class).getValue())
        {
            return super.highlightColor();
        }
        return getGeneralColor();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getPrimaryAccentColor(), getSecondaryAccentColor(), getGeneralColor(), getDisabledColor(), getEnabledColor());
    }
}
