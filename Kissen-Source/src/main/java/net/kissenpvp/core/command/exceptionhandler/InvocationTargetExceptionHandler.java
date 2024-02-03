package net.kissenpvp.core.command.exceptionhandler;

import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.exception.CommandExceptionHandler;
import net.kissenpvp.core.api.networking.client.entitiy.MessageReceiver;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public class InvocationTargetExceptionHandler<S extends ServerEntity> implements CommandExceptionHandler<InvocationTargetException, S> {
    @Override
    public boolean handle(@NotNull CommandPayload<S> commandPayload, java.lang.reflect.@NotNull InvocationTargetException throwable) {
        /* TODO return Optional.ofNullable(throwable.getCause()).map(cause -> KissenCore.getInstance().getImplementation(
                KissenCommandImplementation.class).getInternalHandler().getHandlerService().handleThrowable(
                commandPayload,
                cause)).orElse(false);*/
        return false;
    }
}
