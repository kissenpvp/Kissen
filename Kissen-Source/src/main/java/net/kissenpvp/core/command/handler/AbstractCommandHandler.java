package net.kissenpvp.core.command.handler;

import lombok.AccessLevel;
import lombok.Getter;
import net.kissenpvp.core.api.base.ExceptionHandler;
import net.kissenpvp.core.api.command.ArgumentParser;
import net.kissenpvp.core.api.command.CommandHandler;
import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.annotations.CommandData;
import net.kissenpvp.core.api.command.annotations.TabCompleter;
import net.kissenpvp.core.api.command.exception.CommandExceptionHandler;
import net.kissenpvp.core.api.command.executor.CommandExecutor;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.command.CommandHolder;
import net.kissenpvp.core.command.argument.MethodEvaluator;
import net.kissenpvp.core.command.executor.KissenCommandExecutor;
import net.kissenpvp.core.command.executor.KissenPaperCompleteExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Method;
import java.util.*;

@Getter
public abstract class AbstractCommandHandler<S extends ServerEntity, C extends CommandHolder<S, ? extends C>> implements CommandHandler<S, C>
{
    @Getter(AccessLevel.PROTECTED)
    private final Set<C> commands;
    private final Map<Class<?>, ArgumentParser<?, S>> parser;
    private final Set<ExceptionHandler<?>> exceptionHandler;
    private final MethodEvaluator<S> evaluator;

    public AbstractCommandHandler()
    {
        this.commands = new HashSet<>();
        this.parser = new HashMap<>();
        this.exceptionHandler = new HashSet<>();
        this.evaluator = new MethodEvaluator<>(this::getParser);
    }

    public @NotNull @Unmodifiable Set<ExceptionHandler<?>> getExceptionHandler()
    {
        return Collections.unmodifiableSet(exceptionHandler);
    }

    public @NotNull @Unmodifiable Map<Class<?>, ArgumentParser<?, S>> getParser()
    {
        return Collections.unmodifiableMap(parser);
    }

    @Override
    public @NotNull Optional<C> getCommand(@NotNull String name)
    {
        return getCommands().stream().filter(command -> command.getName().equals(name)).findFirst();
    }

    @Override
    public void registerCommand(@NotNull Object @NotNull ... objects)
    {
        KissenCore.getInstance().getLogger().debug(
                "Class(es) {} has been registered as command holder from handler {}..",
                Arrays.toString(objects),
                getHandlerName());
        for (Object object : objects)
        {
            for (Method method : object.getClass().getDeclaredMethods())
            {
                injectMethod(object, method);
            }
        }
    }

    @Override
    public void registerExceptionHandler(@NotNull ExceptionHandler<?> handler)
    {
        if (exceptionHandler.contains(handler))
        {
            throw new IllegalArgumentException("There is already an exception handler registered for that exception.");
        }
        KissenCore.getInstance().getLogger().debug("Registered exception handler {} in handler {}.",
                handler.getClass().getSimpleName(),
                getHandlerName());
        exceptionHandler.add(handler);
    }

    @Override
    public <T> void registerParser(@NotNull Class<T> type, @NotNull ArgumentParser<T, S> parser)
    {
        if (this.parser.containsKey(type))
        {
            throw new IllegalArgumentException(String.format("Parser type %s is already registered.",
                    type.getSimpleName()));
        }
        KissenCore.getInstance().getLogger().debug("Register parser for type {} with handler {}.",
                type,
                getHandlerName());
        this.parser.put(type, parser);
    }

    protected abstract void registerCommand(@NotNull C command);

    protected abstract @NotNull C buildCommand(@NotNull String name);

    protected abstract @NotNull String getHandlerName();

    private void injectMethod(@NotNull Object instance, @NotNull Method method)
    {
        CommandData commandData = method.getAnnotation(CommandData.class);
        if (commandData != null)
        {
            C command = buildCommand(commandData.value());
            CommandExecutor<S> executor = new KissenCommandExecutor<>(this, instance, method)
            {
                @Override
                protected boolean handleThrowable(@NotNull CommandPayload<S> commandPayload, @NotNull Throwable throwable)
                {
                    return AbstractCommandHandler.this.handleThrowable(commandPayload, throwable);
                }
            };
            command.initCommand(commandData, executor);
            if (command.getPosition() == 0)
            {
                registerCommand(command);
            }
            return;
        }

        TabCompleter tabCompleter = method.getAnnotation(TabCompleter.class);
        if (tabCompleter != null)
        {
            KissenPaperCompleteExecutor<S> executor = new KissenPaperCompleteExecutor<>(instance, method);
            buildCommand(tabCompleter.value()).initCompleter(executor);
        }
    }

    public boolean handleThrowable(@NotNull CommandPayload<S> commandPayload, @NotNull Throwable throwable)
    {
        return getExceptionHandler().stream().anyMatch(handler -> handleIndividualHandler(handler,
                commandPayload,
                throwable));
    }

    private <T extends Throwable> boolean handleIndividualHandler(@NotNull ExceptionHandler<T> handler, @NotNull CommandPayload<S> commandPayload, @NotNull Throwable throwable)
    {
        try
        {
            return handleExceptionHandler(handler, commandPayload, throwable);
        }
        catch (ClassCastException ignored)
        {
            // Intentionally ignored
        }
        return false;
    }

    private <T extends Throwable> boolean handleExceptionHandler(@NotNull ExceptionHandler<T> handler, @NotNull CommandPayload<S> commandPayload, @NotNull Throwable throwable)
    {
        if (handler instanceof CommandExceptionHandler)
        {
            return handleCommandExceptionHandler((CommandExceptionHandler<T, S>) handler,
                    commandPayload,
                    (T) throwable);
        }
        else
        {
            return handler.handle((T) throwable);
        }
    }

    private <T extends Throwable> boolean handleCommandExceptionHandler(@NotNull CommandExceptionHandler<T, S> exceptionHandler, @NotNull CommandPayload<S> commandPayload, @NotNull T throwable)
    {
        return exceptionHandler.handle(commandPayload, throwable);
    }
}
