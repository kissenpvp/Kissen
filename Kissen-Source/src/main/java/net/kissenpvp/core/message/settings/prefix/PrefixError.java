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

package net.kissenpvp.core.message.settings.prefix;


import net.kissenpvp.core.message.EnumColorProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public class PrefixError extends OptionPrefix {
    @Override
    public @NotNull String getDescription() {
        return "Prefix for messages seen when something went wrong.";
    }

    @Override
    public @NotNull Component getDefault() {
        return Component.text("Kissen")
                .color(EnumColorProvider.PRIMARY.getTextColor())
                .append(Component.text("PvP").color(EnumColorProvider.SECONDARY.getTextColor()))
                .append(Component.space())
                .append(Component.text("»").color(NamedTextColor.DARK_RED))
                .append(Component.empty().color(EnumColorProvider.DEFAULT.getTextColor()));
    }

    @Override
    public int getPriority() {
        return 1;
    }
}