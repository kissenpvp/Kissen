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

package net.kissenpvp.core.message.serializer;

import net.kissenpvp.core.api.base.serializer.TextSerializer;
import net.kissenpvp.core.message.EnumColorProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

public class KissenMiniSerializer implements TextSerializer<Component> {
    @Override
    public @NotNull String serialize(@NotNull Component object) {
        String string = MiniMessage.miniMessage().serialize(object);
        for (EnumColorProvider enumColorProvider : EnumColorProvider.values()) {
            string = string.replace("<" + enumColorProvider.getTextColor().asHexString() + ">",
                    "<" + enumColorProvider + ">").replace("</" + enumColorProvider.getTextColor().asHexString() +
                    ">", "</" + enumColorProvider + ">");
        }
        return string;
    }

    @Override
    public @NotNull Component deserialize(@NotNull String input) {
        String string = input;
        for (EnumColorProvider enumColorProvider : EnumColorProvider.values()) {
            string = string.replace("<" + enumColorProvider.toString() + ">",
                    "<" + enumColorProvider.getTextColor().asHexString() + ">").replace("</" + enumColorProvider +
                    ">", "</" + enumColorProvider.getTextColor().asHexString() + ">");
        }
        //todo prob use minimessage tags https://docs.advntr.dev/minimessage/api.html#inserting
        return MiniMessage.miniMessage().deserialize(string);
    }
}
