package net.kissenpvp.core.command.exceptionhandler;

import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.exception.ExecutionException;
import net.kissenpvp.core.api.networking.client.entitiy.MessageReceiver;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import org.jetbrains.annotations.NotNull;

public class ExecutionExceptionHandler<S extends ServerEntity> implements net.kissenpvp.core.api.command.exception.CommandExceptionHandler<ExecutionException, S> {
    @Override
    public boolean handle(@NotNull CommandPayload<S> commandPayload, @NotNull ExecutionException throwable) {
        if(commandPayload.getSender() instanceof MessageReceiver receiver)
        {
            receiver.getKyoriAudience().sendMessage(throwable.getComponent());
            return true;
        }
        return false;
    }
}
