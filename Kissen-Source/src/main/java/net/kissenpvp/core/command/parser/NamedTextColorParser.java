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

package net.kissenpvp.core.command.parser;

import net.kissenpvp.core.api.command.ArgumentParser;
import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.command.InvalidColorException;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;

public class NamedTextColorParser <S extends ServerEntity> implements ArgumentParser<NamedTextColor, S> {
    @Override
    public @NotNull String serialize(@NotNull NamedTextColor object) {
        return object.examinableName();
    }

    @Override
    public @NotNull NamedTextColor deserialize(@NotNull String input) {
        try
        {
            return Objects.requireNonNull(NamedTextColor.NAMES.value(input));
        }
        catch (NullPointerException nullPointerException)
        {
            throw new InvalidColorException(input);
        }
    }

    @Override
    public @Nullable String argumentName()
    {
        return "color";
    }

    @Override
    public @NotNull Collection<String> tabCompletion(@NotNull CommandPayload<S> commandPayload) {
        return NamedTextColor.NAMES.keys();
    }
}
