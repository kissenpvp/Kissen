package net.kissenpvp.core.command.handler;

import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.exception.CommandException;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.command.KissenCommandImplementation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CommandExceptionHandler<S extends ServerEntity> implements net.kissenpvp.core.api.command.exception.CommandExceptionHandler<CommandException, S> {
    @Override
    public boolean handle(@NotNull CommandPayload<S> commandPayload, @NotNull CommandException throwable) {
        //Basically pass through or return false if no suitable handler has been found
        return Optional.ofNullable(throwable.getCause()).map(cause -> KissenCore.getInstance().getImplementation(KissenCommandImplementation.class).handle(commandPayload, cause)).orElse(false);
    }
}
