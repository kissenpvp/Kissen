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

import net.kissenpvp.core.api.message.ChatImplementation;
import net.kissenpvp.core.api.message.ThemeProvider;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class KissenChatImplementation implements ChatImplementation
{
    @Override
    public @NotNull Optional<Component> prepareMessage(@NotNull ServerEntity sender, @NotNull ServerEntity serverEntity, @NotNull Component @NotNull ... components)
    {
        Component prefix = Component.text("Kissen").color(ThemeProvider.primary()).append(
                Component.text("PvP").color(ThemeProvider.secondary())).append(
                Component.text(" » ").color(ThemeProvider.general()));
        Component[] components1 = new Component[components.length + 1];
        components1[0] = prefix;
        System.arraycopy(components, 0, components1, 1, components.length + 1 - 1);
        return Optional.of(styleComponent(serverEntity, components1));
    }

    @Override
    public @NotNull Component styleComponent(@NotNull ServerEntity serverEntity, @NotNull Component... components)
    {
        return serverEntity.getTheme().style(components);
    }
}
