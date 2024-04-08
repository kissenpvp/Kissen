package net.kissenpvp.core.command.exceptionhandler;

import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.exception.AbstractCommandExceptionHandler;
import net.kissenpvp.core.api.networking.client.entitiy.MessageReceiver;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.command.InvalidColorException;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;


public class InvalidColorExceptionHandler<S extends ServerEntity> implements AbstractCommandExceptionHandler<InvalidColorException, S>
{
    @Override
    public boolean handle(@NotNull CommandPayload<S> commandPayload, @NotNull InvalidColorException throwable)
    {
        if(commandPayload.getSender() instanceof MessageReceiver messageReceiver)
        {
            messageReceiver.getKyoriAudience().sendMessage(Component.translatable("server.command.invalid.color", Component.text(throwable.getInput())));
        }
        return true;
    }
}
