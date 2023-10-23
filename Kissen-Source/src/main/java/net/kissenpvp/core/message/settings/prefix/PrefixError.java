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

package net.kissenpvp.core.message.settings.prefix;


import net.kissenpvp.core.api.message.ThemeProvider;
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
                .color(ThemeProvider.primary())
                .append(Component.text("PvP").color(ThemeProvider.secondary()))
                .append(Component.space())
                .append(Component.text("»").color(NamedTextColor.DARK_RED))
                .append(Component.empty().color(ThemeProvider.general()));
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
