package net.kissenpvp.core.command.handler;

import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.exception.CommandExceptionHandler;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.command.KissenCommandImplementation;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class InvocationTargetExceptionHandler<S extends ServerEntity> implements CommandExceptionHandler<InvocationTargetException, S> {
    @Override
    public boolean handle(@NotNull CommandPayload<S> commandPayload, java.lang.reflect.@NotNull InvocationTargetException throwable) {
        return Optional.ofNullable(throwable.getCause()).map(cause -> KissenCore.getInstance().getImplementation(KissenCommandImplementation.class).handle(commandPayload, cause)).orElse(false);
    }
}
