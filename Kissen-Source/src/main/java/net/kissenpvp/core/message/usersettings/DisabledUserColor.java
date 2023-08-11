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

package net.kissenpvp.core.message.usersettings;

import net.kissenpvp.core.api.config.ConfigurationImplementation;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.message.settings.DefaultDisabledColor;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public class DisabledUserColor extends UserColorSetting
{
    @Override public @NotNull String getKey()
    {
        return "disabledcolor";
    }

    @Override
    public @NotNull NamedTextColor getDefaultValue()
    {
        return KissenCore.getInstance().getImplementation(ConfigurationImplementation.class).getSetting(DefaultDisabledColor.class);
    }
}
