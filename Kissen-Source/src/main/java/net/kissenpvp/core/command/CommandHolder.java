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

import net.kissenpvp.core.api.command.executor.CommandExecutor;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Function;

public interface CommandHolder<S extends ServerEntity, C extends CommandHolder<S, C>> extends Iterable<CommandHolder<?, ?>> {

    @NotNull String getName();

    @NotNull String getFullName();

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
