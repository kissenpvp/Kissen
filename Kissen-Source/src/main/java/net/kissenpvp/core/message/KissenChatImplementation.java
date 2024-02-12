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
import net.kissenpvp.core.api.message.event.SystemMessageEvent;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.event.EventImplementation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class KissenChatImplementation implements ChatImplementation
{
    @Override
    public @NotNull Optional<Component> prepareMessage(@NotNull ServerEntity sender, @NotNull ServerEntity receiver, @NotNull Component @NotNull ... components)
    {
        if(!receiver.isConnected())
        {
            return Optional.of(Component.join(JoinConfiguration.noSeparators(), components));
        }

        SystemMessageEvent systemMessageEvent = new SystemMessageEvent(sender, receiver, components);
        if(!KissenCore.getInstance().getImplementation(EventImplementation.class).call(systemMessageEvent))
        {
            return Optional.empty();
        }

        return Optional.of(systemMessageEvent.getComponent());

        /*Predicate<Component> contain = component -> component.contains(Component.newline());
        boolean append = components.length < 2 && Arrays.stream(components).noneMatch(contain);

        if (!append || (receiver instanceof PlayerClient<?, ?, ?> player && !player.getUserSetting(ShowPrefix.class).getValue()))
        {
            return Optional.of(styleComponent(receiver, components));
        }

        Component prefix = getPrefix(receiver);

        Component[] prefixAppendedComponents = new Component[components.length + 1];
        prefixAppendedComponents[0] = prefix.appendSpace().append(Component.text("»")).appendSpace();
        System.arraycopy(components, 0, prefixAppendedComponents, 1, components.length);
        return Optional.of(styleComponent(receiver, prefixAppendedComponents));*/
    }


    /*private @NotNull Component getPrefix(@NotNull ServerEntity serverEntity)
    {
        final MiniMessage miniMessage = MiniMessage.miniMessage();
        String primary = serverEntity.getTheme().getPrimaryAccentColor().asHexString();
        String secondary = serverEntity.getTheme().getSecondaryAccentColor().asHexString();

        ConfigurationImplementation config = KissenCore.getInstance().getImplementation(ConfigurationImplementation.class);
        String systemPrefix = config.getSetting(DefaultSystemPrefix.class);
        if (serverEntity instanceof PlayerClient<?, ?, ?> player)
        {
            systemPrefix = player.getUserSetting(SystemPrefix.class).getValue();
        }

        String base = "<gradient:%s:%s>%s</gradient>";

        return MiniMessage.miniMessage().deserialize(base.formatted(primary, secondary, systemPrefix));
    }*/
}
