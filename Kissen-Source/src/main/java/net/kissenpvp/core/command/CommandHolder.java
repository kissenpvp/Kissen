package net.kissenpvp.core.command;

import net.kissenpvp.core.api.command.executor.CommandExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public interface CommandHolder<S, C extends CommandHolder<S, C>> extends Iterable<CommandHolder<?, ?>> {

    @NotNull String getName();

    @NotNull String getFullName();

    int getPosition();

    @NotNull CommandExecutor<S> getExecutor();

    default @NotNull Optional<CommandHolder<?, ?>> getParent() {
        return Optional.empty();
    }

    @NotNull @Unmodifiable List<C> getChildCommandList();

    @NotNull Optional<C> getChildCommand(@NotNull String name);

    @NotNull CommandInfo getCommandInfo();

    default boolean equals(@NotNull String name) {
        return getName().equalsIgnoreCase(name) || Arrays.stream(getCommandInfo().getAliases())
                .anyMatch(alias -> alias.equalsIgnoreCase(name));
    }

    @NotNull
    @Override
    default Iterator<CommandHolder<?, ?>> iterator() {
        return new CommandInfoIterator(this);
    }
}
