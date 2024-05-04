package net.kissenpvp.core.command.exceptionhandler;

import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.exception.AbstractCommandExceptionHandler;
import net.kissenpvp.core.api.networking.client.entitiy.MessageReceiver;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class NumberFormatExceptionHandler<S extends ServerEntity> implements AbstractCommandExceptionHandler<NumberFormatException, S> {
    @Override
    public boolean handle(@NotNull CommandPayload<S> commandPayload, @NotNull NumberFormatException throwable) {
        if(commandPayload.getSender() instanceof MessageReceiver messageReceiver)
        {
            messageReceiver.getKyoriAudience().sendMessage(Component.text("Make sure to insert a number."));
            return true;
        }

        return false;
    }
}
