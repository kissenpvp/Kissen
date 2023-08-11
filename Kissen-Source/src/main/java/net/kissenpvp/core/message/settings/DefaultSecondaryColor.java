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

package net.kissenpvp.core.message.settings;

import net.kissenpvp.core.api.config.options.OptionNamedTextColor;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;


public class DefaultSecondaryColor extends OptionNamedTextColor
{
    @Override public @NotNull String getGroup()
    {
        return "appearance";
    }

    @Override public @NotNull String getDescription()
    {
        return "Theming for messages that are somehow important but not as much as some other.";
    }

    @Override public @NotNull NamedTextColor getDefault()
    {
        return NamedTextColor.GOLD;
    }

    @Override public int getPriority()
    {
        return 12;
    }
}
