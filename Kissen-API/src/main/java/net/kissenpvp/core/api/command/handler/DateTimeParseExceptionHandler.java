package net.kissenpvp.core.api.command.handler;

import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.exception.AbstractCommandExceptionHandler;
import net.kissenpvp.core.api.networking.client.entitiy.MessageReceiver;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeParseException;

public class DateTimeParseExceptionHandler<S extends ServerEntity> implements AbstractCommandExceptionHandler<DateTimeParseException, S> {
    @Override
    public boolean handle(@NotNull CommandPayload<S> commandPayload, @NotNull DateTimeParseException throwable) {

        if(commandPayload.getSender() instanceof MessageReceiver messageReceiver)
        {
            messageReceiver.getKyoriAudience().sendMessage(Component.translatable("server.command.invalid-duration"));
            return true;
        }

        return false;
    }
}
