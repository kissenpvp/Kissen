package net.kissenpvp.core.api.command;

import net.kissenpvp.core.api.base.ExceptionHandler;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface CommandHandler<S extends ServerEntity, C>
{
    @NotNull Optional<C> getCommand(@NotNull String name);

    void registerCommand(@NotNull Object... objects);

    void registerExceptionHandler(@NotNull ExceptionHandler<?> exceptionHandler);

    <T> void registerParser(@NotNull Class<T> type, @NotNull ArgumentParser<T, S> parser);
}
