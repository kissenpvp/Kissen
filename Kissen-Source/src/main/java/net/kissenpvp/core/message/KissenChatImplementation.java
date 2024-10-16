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
    public @NotNull Optional<Component> handle(@NotNull ServerEntity sender, @NotNull ServerEntity receiver, @NotNull Component @NotNull ... components)
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
    }
}
