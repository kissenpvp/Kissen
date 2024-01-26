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

package net.kissenpvp.core.command;

import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.annotations.CommandData;
import net.kissenpvp.core.api.command.executor.CommandExecutor;
import net.kissenpvp.core.api.command.executor.TabCompleterExecutor;
import net.kissenpvp.core.api.message.ThemeProvider;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Function;

public interface CommandHolder<S extends ServerEntity, C extends CommandHolder<S, C>> extends Iterable<CommandHolder<?, ?>> {


    /**
     * Initializes the command with the provided command data and executor.
     * <p>
     * This method is used to set up the command with the given data and to assign an executor to handle
     * the command execution. This setup process is typically performed during command registration.
     *
     * @param commandData     The details about the command (e.g., name, description, usage). Must not be null.
     * @param commandExecutor The executor to handle the command execution. Must not be null.
     * @throws NullPointerException if any of the parameters is null.
     */
    void initCommand(@NotNull CommandData commandData, @NotNull CommandExecutor<S> commandExecutor);

    /**
     * Initializes the command with a given tab completer.
     * <p>
     * This method is used to set up a tab completion system for the command, which assists the user by
     * filling in possible command arguments as they type. This method is typically called when the command is registered.
     *
     * @param completerExecutor The tab completion executor to register. Must not be null.
     * @throws NullPointerException if the completerExecutor is null.
     */
    void initCompleter(@NotNull TabCompleterExecutor<S> completerExecutor);

    default @NotNull Component getFormattedUsage(@NotNull CommandPayload<S> payload)
    {
        TextComponent.Builder builder = Component.text();
        String[] usages = getUsage().split(";");
        for (String usage : usages)
        {
            Component usageComponent = Component.text(usage).color(ThemeProvider.general());
            builder.appendNewline().append(Component.text("» ")).append(usageComponent);
        }
        return Component.translatable("server.command.incorrect-usage", builder);
    }

    @NotNull String getName();

    @NotNull String getFullName();


    @NotNull String getUsage();

    int getPosition();

    @NotNull CommandExecutor<S> getExecutor();

    default @NotNull Optional<CommandHolder<?, ?>> getParent() {
        return Optional.empty();
    }

    @NotNull @Unmodifiable List<C> getChildCommandList();

    @NotNull Optional<C> getChildCommand(@NotNull String name);

    @NotNull Optional<CommandInfo> getCommandInfo();

    default boolean equals(@NotNull String name) {
        return getName().equalsIgnoreCase(name) || getCommandInfo().map(
                info -> Arrays.stream(info.getAliases()).anyMatch(alias -> alias.equalsIgnoreCase(name))).orElse(false);
    }

    @NotNull
    @Override
    default Iterator<CommandHolder<?, ?>> iterator() {
        return new CommandInfoIterator(this);
    }
}
