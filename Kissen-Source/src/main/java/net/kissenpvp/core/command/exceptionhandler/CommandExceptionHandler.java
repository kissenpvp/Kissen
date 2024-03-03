package net.kissenpvp.core.command.exceptionhandler;

import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.exception.CommandException;
import net.kissenpvp.core.api.networking.client.entitiy.MessageReceiver;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import org.jetbrains.annotations.NotNull;

public class CommandExceptionHandler<S extends ServerEntity> implements net.kissenpvp.core.api.command.exception.CommandExceptionHandler<CommandException, S> {
    @Override
    public boolean handle(@NotNull CommandPayload<S> commandPayload, @NotNull CommandException throwable) {
        if(commandPayload.getSender() instanceof MessageReceiver receiver)
        {
            receiver.getKyoriAudience().sendMessage(throwable.getComponent());
            return true;
        }
        return false;
    }
}
