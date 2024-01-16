package net.kissenpvp.core.event;


import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.exception.CommandExceptionHandler;
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.networking.client.entitiy.MessageReceiver;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class EventCancelledHandler<S extends ServerEntity> implements CommandExceptionHandler<EventCancelledException, S>
{
    @Override
    public boolean handle(@NotNull CommandPayload<S> commandPayload, @NotNull EventCancelledException throwable)
    {
        if(commandPayload.getSender() instanceof MessageReceiver messageReceiver)
        {
            messageReceiver.getKyoriAudience().sendMessage(Component.translatable("server.event.cancel"));
            return true;
        }

        return false;
    }
}
