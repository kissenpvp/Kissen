package net.kissenpvp.core.api.command.handler;

import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.exception.CommandExceptionHandler;
import net.kissenpvp.core.api.networking.client.entitiy.MessageReceiver;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class IllegalAccessHandler<S extends ServerEntity> implements CommandExceptionHandler<IllegalAccessError, S> {
    @Override
    public boolean handle(@NotNull CommandPayload<S> commandPayload, @NotNull IllegalAccessError throwable) {
        if(commandPayload.getSender() instanceof MessageReceiver messageReceiver)
        {
            messageReceiver.getKyoriAudience().sendMessage(Component.translatable("commands.help.failed"));
            return true;
        }
        return false;
    }
}
