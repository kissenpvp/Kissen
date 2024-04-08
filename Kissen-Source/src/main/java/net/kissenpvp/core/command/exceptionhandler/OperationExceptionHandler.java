package net.kissenpvp.core.command.exceptionhandler;

import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.exception.AbstractCommandExceptionHandler;
import net.kissenpvp.core.api.command.exception.OperationException;
import net.kissenpvp.core.api.networking.client.entitiy.MessageReceiver;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import org.jetbrains.annotations.NotNull;

public class OperationExceptionHandler<S extends ServerEntity> implements AbstractCommandExceptionHandler<OperationException, S> {
    @Override
    public boolean handle(@NotNull CommandPayload<S> commandPayload, @NotNull OperationException throwable) {
        if (commandPayload.getSender() instanceof MessageReceiver receiver) {
            throwable.getComponent().ifPresent(component -> receiver.getKyoriAudience().sendMessage(component));
            return true;
        }
        return false;
    }
}
