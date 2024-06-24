package net.kissenpvp.core.command.handler;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.kissenpvp.core.api.base.ExceptionHandler;
import net.kissenpvp.core.api.command.AbstractArgumentParser;
import net.kissenpvp.core.api.command.CommandHandler;
import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.annotations.CommandData;
import net.kissenpvp.core.api.command.annotations.TabCompleter;
import net.kissenpvp.core.api.command.exception.AbstractCommandExceptionHandler;
import net.kissenpvp.core.api.command.executor.CommandExecutor;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.command.CommandHolder;
import net.kissenpvp.core.command.argument.MethodEvaluator;
import net.kissenpvp.core.command.executor.KissenCommandExecutor;
import net.kissenpvp.core.command.executor.KissenCompleteExecutor;
import net.kissenpvp.core.permission.InternalPermissionImplementation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

@Getter
@Slf4j(topic = "Kissen")
public abstract class AbstractCommandHandler<S extends ServerEntity, C extends CommandHolder<S, ? extends C>> implements CommandHandler<S, C> {
    @Getter(AccessLevel.PROTECTED)
    private final Set<C> commands;
    private final Map<Class<?>, AbstractArgumentParser<?, S>> parser;
    private final Set<ExceptionHandler<?>> exceptionHandler;
    private final MethodEvaluator<S> evaluator;
    private final Set<Object> cached;
    private boolean initialized;

    public AbstractCommandHandler() {
        this.cached = new HashSet<>();
        this.commands = new HashSet<>();
        this.parser = new HashMap<>();
        this.exceptionHandler = new HashSet<>();
        this.initialized = false;
        this.evaluator = new MethodEvaluator<>(this::getParser);
    }

    private static @NotNull CommandData dummyCommandData(@NotNull String rootName) {
        InvocationHandler handler = (proxy, method, args) -> Objects.requireNonNullElse(method.getDefaultValue(), rootName);
        ClassLoader loader = CommandData.class.getClassLoader();
        return (CommandData) Proxy.newProxyInstance(loader, new Class[]{CommandData.class}, handler);
    }

    public @NotNull @Unmodifiable Set<ExceptionHandler<?>> getExceptionHandler() {
        return Collections.unmodifiableSet(exceptionHandler);
    }

    public @NotNull @Unmodifiable Map<Class<?>, AbstractArgumentParser<?, S>> getParser() {
        return Collections.unmodifiableMap(parser);
    }

    @Override
    public @NotNull Optional<C> getCommand(@NotNull String name) {
        return getCommands().stream().filter(command -> command.getFullName().equals(name)).findFirst();
    }

    @Override
    public void registerCommand(@NotNull Object @NotNull ... objects) {
        if (isInitialized()) {
            throw new IllegalStateException("Handler is already initialized.");
        }
        cached.addAll(List.of(objects));
    }

    @Override
    public void registerExceptionHandler(@NotNull ExceptionHandler<?> handler) {
        if (exceptionHandler.contains(handler)) {
            throw new IllegalArgumentException("There is already an exception handler registered for that exception.");
        }
        log.debug("Registered exception handler {} in handler {}.", handler.getClass().getSimpleName(), getHandlerName());
        exceptionHandler.add(handler);
    }

    @Override
    public <T> void registerParser(@NotNull Class<T> type, @NotNull AbstractArgumentParser<T, S> parser) {
        if (this.parser.containsKey(type)) {
            throw new IllegalArgumentException(String.format("Parser type %s is already registered.", type.getSimpleName()));
        }
        log.debug("Register parser for type {} with handler {}.", type, getHandlerName());
        this.parser.put(type, parser);
    }

    public void registerCachedCommands() {
        List<CommandContainer<CommandData>> commands = new ArrayList<>();
        List<CommandContainer<TabCompleter>> tabCompleter = new ArrayList<>();
        for (Object object : cached) {
            for (Method method : object.getClass().getDeclaredMethods()) {
                CommandData commandData = method.getAnnotation(CommandData.class);
                if (Objects.nonNull(commandData)) {
                    commands.add(new CommandContainer<>(commandData, object, method));
                }

                TabCompleter tabComplete = method.getAnnotation(TabCompleter.class);
                if (Objects.nonNull(tabComplete)) {
                    tabCompleter.add(new CommandContainer<>(tabComplete, object, method));
                }
            }
        }

        commands.sort(Comparator.comparingInt(o -> o.annotation().value().split("\\.").length));
        commands.forEach(this::loadCommand);
        tabCompleter.forEach(this::loadTabCompleter);

        log.debug("Class(es) {} has been registered as command holder from handler {}.", cached, getHandlerName());
        cached.clear();
        initialized = true;
    }

    public void unregister() {
        getCommands().stream().filter(command -> command.getPosition() == 0).forEach(this::unregisterCommand);
    }

    private void loadCommand(@NotNull CommandContainer<CommandData> data) {

        C command = buildCommand(data.annotation().value());
        CommandExecutor<S> executor = new KissenCommandExecutor<>(this, data.container(), data.method()) {
            @Override
            protected boolean handleThrowable(@NotNull CommandPayload<S> commandPayload, @NotNull Throwable throwable) {
                do {
                    if (AbstractCommandHandler.this.handleThrowable(commandPayload, throwable)) {
                        return true;
                    }
                } while ((throwable = throwable.getCause()) != null);
                return false;
            }
        };
        command.initCommand(data.annotation(), executor);
        addPermissions(command);

        if (command.getPosition() == 0) {
            registerCommand(command);
            return;
        }

        // No root registered? No worries the system got you covered
        String rootName = command.getFullName().split("\\.")[0];
        C root = getCommands().stream().filter(c -> Objects.equals(c.getName(), rootName)).findFirst().orElseThrow();
        if (!root.isRegistered()) {
            root.initCommand(dummyCommandData(rootName), executor);
            getCommands().add(root);
            registerCommand(root);
        }
    }

    private void loadTabCompleter(@NotNull CommandContainer<TabCompleter> completer) {
        KissenCompleteExecutor<S> executor = new KissenCompleteExecutor<>(completer.container(), completer.method());
        buildCommand(completer.annotation().value()).initCompleter(executor);
    }

    private void addPermissions(@NotNull C command) {
        if (command.getPermission() != null) {
            for (String permission : command.getPermission().split(";")) {
                InternalPermissionImplementation<?> implementation = KissenCore.getInstance().getImplementation(InternalPermissionImplementation.class);
                implementation.addPermission(permission);
            }
        }
    }

    public boolean handleThrowable(@NotNull CommandPayload<S> commandPayload, @NotNull Throwable throwable) {
        return getExceptionHandler().stream().anyMatch(handler -> handleIndividualHandler(handler, commandPayload, throwable));
    }

    private <T extends Throwable> boolean handleIndividualHandler(@NotNull ExceptionHandler<T> handler, @NotNull CommandPayload<S> commandPayload, @NotNull Throwable throwable) {
        try {
            return handleExceptionHandler(handler, commandPayload, throwable);
        } catch (ClassCastException ignored) {
            // Intentionally ignored
        }
        return false;
    }

    private <T extends Throwable> boolean handleExceptionHandler(@NotNull ExceptionHandler<T> handler, @NotNull CommandPayload<S> commandPayload, @NotNull Throwable throwable) {
        if (handler instanceof AbstractCommandExceptionHandler) {
            return handleCommandExceptionHandler((AbstractCommandExceptionHandler<T, S>) handler, commandPayload, (T) throwable);
        } else {
            return handler.handle((T) throwable);
        }
    }

    private <T extends Throwable> boolean handleCommandExceptionHandler(@NotNull AbstractCommandExceptionHandler<T, S> exceptionHandler, @NotNull CommandPayload<S> commandPayload, @NotNull T throwable) {
        return exceptionHandler.handle(commandPayload, throwable);
    }

    protected abstract void registerCommand(@NotNull C command);

    protected abstract @NotNull C buildCommand(@NotNull String name);

    protected abstract @NotNull String getHandlerName();

    protected abstract void unregisterCommand(@NotNull C command);

    private record CommandContainer<T extends Annotation>(@NotNull T annotation, @NotNull Object container,
                                                          @NotNull Method method) {
    }
}
