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
