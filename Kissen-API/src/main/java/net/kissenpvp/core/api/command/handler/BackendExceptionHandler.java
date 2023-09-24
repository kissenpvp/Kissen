package net.kissenpvp.core.api.command.handler;

import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.exception.CommandExceptionHandler;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.networking.client.entitiy.MessageReceiver;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class BackendExceptionHandler<S extends ServerEntity> implements CommandExceptionHandler<BackendException, S> {
    @Override
    public boolean handle(@NotNull CommandPayload<S> commandPayload, @NotNull BackendException throwable) {
        if(commandPayload.getSender() instanceof MessageReceiver messageReceiver)
        {
            messageReceiver.getKyoriAudience().sendMessage(Component.translatable("server.command.backend-exception"));
            return true;
        }

        return false;
    }
}
