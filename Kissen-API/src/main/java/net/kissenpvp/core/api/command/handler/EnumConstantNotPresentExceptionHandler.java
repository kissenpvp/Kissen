package net.kissenpvp.core.api.command.handler;

import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.exception.ArgumentMissingException;
import net.kissenpvp.core.api.command.exception.CommandExceptionHandler;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import org.jetbrains.annotations.NotNull;

public class EnumConstantNotPresentExceptionHandler<S extends ServerEntity> implements CommandExceptionHandler<EnumConstantNotPresentException, S> {
    @Override
    public boolean handle(@NotNull CommandPayload<S> commandPayload, @NotNull EnumConstantNotPresentException throwable) {
        return new ArgumentMissingExceptionHandler<S>().handle(commandPayload, new ArgumentMissingException());
    }
}
