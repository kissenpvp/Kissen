package net.kissenpvp.core.command.exceptionhandler;

import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.exception.AbstractCommandExceptionHandler;
import net.kissenpvp.core.api.networking.client.entitiy.MessageReceiver;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class NullPointerExceptionHandler<S extends ServerEntity> implements AbstractCommandExceptionHandler<NullPointerException, S>
{

    @Override
    public boolean handle(@NotNull CommandPayload<S> commandPayload, @NotNull NullPointerException throwable)
    {
        if(commandPayload.getSender() instanceof MessageReceiver messageReceiver)
        {
            messageReceiver.getKyoriAudience().sendMessage(Component.translatable("command.unknown.argument"));
        }
        return false;
    }
}
