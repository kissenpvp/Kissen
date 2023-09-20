package net.kissenpvp.core.api.command.exception;

import net.kissenpvp.core.api.base.ExceptionHandler;
import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import org.jetbrains.annotations.NotNull;

public interface CommandExceptionHandler<T extends Throwable, S extends ServerEntity> extends ExceptionHandler<T> {

    @Override
    default boolean handle(@NotNull T throwable) { return true; }

    boolean handle(@NotNull CommandPayload<S> commandPayload, @NotNull T throwable);
}
