package net.kissenpvp.core.command.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kissenpvp.core.api.base.ExceptionHandler;
import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import net.kissenpvp.core.api.command.AbstractArgumentParser;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.command.CommandHolder;
import net.kissenpvp.core.command.CommandImplementation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

@Getter
@RequiredArgsConstructor
public abstract class PluginCommandHandler<S extends ServerEntity, C extends CommandHolder<S, ? extends C>> extends AbstractCommandHandler<S, C>
{
    private final KissenPlugin plugin;

    @Override
    public @NotNull Map<Class<?>, AbstractArgumentParser<?, S>> getParser()
    {
        KissenCore kissen = KissenCore.getInstance();
        CommandImplementation<S> command = kissen.getImplementation(CommandImplementation.class);
        Map<Class<?>, AbstractArgumentParser<?, S>> parserMap = new HashMap<>(command.getInternalHandler().getParser());
        parserMap.putAll(super.getParser());
        return Collections.unmodifiableMap(parserMap);
    }

    @Override
    public @NotNull @Unmodifiable Set<ExceptionHandler<?>> getExceptionHandler()
    {
        KissenCore kissen = KissenCore.getInstance();
        CommandImplementation<S> command = kissen.getImplementation(CommandImplementation.class);
        Set<ExceptionHandler<?>> exceptionHandlers = new HashSet<>(super.getExceptionHandler());
        exceptionHandlers.addAll(command.getInternalHandler().getExceptionHandler());
        return exceptionHandlers;
    }

    @Override
    protected @NotNull String getHandlerName()
    {
        return getPlugin().getName();
    }
}
