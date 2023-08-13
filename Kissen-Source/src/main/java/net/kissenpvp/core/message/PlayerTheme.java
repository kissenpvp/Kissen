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

import lombok.AllArgsConstructor;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.message.usersettings.*;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull TextColor getDefaultColor() {
        return playerClient.getUserSetting(DefaultUserColor.class).getValue();
    }

    @Override
    public @NotNull TextColor getEnabledColor() {
        return playerClient.getUserSetting(EnabledUserColor.class).getValue();
    }

    @Override
    public @NotNull TextColor getDisabledColor() {
        return playerClient.getUserSetting(DisabledUserColor.class).getValue();
    }
}
