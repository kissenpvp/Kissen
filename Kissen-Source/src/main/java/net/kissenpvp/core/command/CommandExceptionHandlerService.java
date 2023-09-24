package net.kissenpvp.core.command;

import net.kissenpvp.core.api.base.ExceptionHandler;
import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.exception.CommandExceptionHandler;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Provides functionality for registering and invoking ExceptionHandlers.
 */
public class CommandExceptionHandlerService {

    private final Set<ExceptionHandler<?>> exceptionHandlerSet;

    /**
     * Constructor for the CommandExceptionHandlerService class.
     * Initializes the exceptionHandlerSet with an empty HashSet.
     */
    public CommandExceptionHandlerService() {
        exceptionHandlerSet = new HashSet<>();
    }

    /**
     * Registers an ExceptionHandler.
     *
     * @param exceptionHandler The ExceptionHandler to be registered.
     */
    public void registerHandler(@NotNull ExceptionHandler<?> exceptionHandler) {
        exceptionHandlerSet.add(exceptionHandler);
    }

    /**
     * Process a Throwable using the registered ExceptionHandlers.
     *
     * @param commandPayload The command payload involved in the occurrence of the throwable.
     * @param throwable      The Throwable object to be processed.
     */
    public <S extends ServerEntity> boolean handleThrowable(@NotNull CommandPayload<S> commandPayload, @NotNull Throwable throwable) {
        return exceptionHandlerSet.stream().anyMatch(handler -> handleIndividualHandler(handler, commandPayload, throwable));
    }

    /**
     * Process a Throwable using an individual ExceptionHandler.
     * If the exception cannot be handled(as the handler supports another type of exception), it will be ignored.
     *
     * @param handler        The ExceptionHandler to be used for handling the exception.
     * @param commandPayload The CommandPayload involved in the occurrence of the exception.
     * @param throwable      The Throwable object to be processed.
     */
    private <T extends Throwable, S extends ServerEntity> boolean handleIndividualHandler(@NotNull ExceptionHandler<T> handler, @NotNull CommandPayload<S> commandPayload, Throwable throwable) {
        try {
            if (handler instanceof CommandExceptionHandler<?, ?> commandExceptionHandler) {
                return handleCommandExceptionHandler(commandExceptionHandler, commandPayload, throwable);
            } else {
                return handler.handle((T) throwable);
            }
        } catch (ClassCastException ignored) {
            // Intentionally ignored
        }
        return false;
    }

    /**
     * Process a CommandPayload using a CommandExceptionHandler.
     *
     * @param handler        The CommandExceptionHandler.
     * @param commandPayload The CommandPayload to be processed.
     * @param throwable      The Throwable object that occurred during command execution.
     */
    private <T extends Throwable, S extends ServerEntity> boolean handleCommandExceptionHandler(@NotNull CommandExceptionHandler<T, S> handler, CommandPayload<? extends ServerEntity> commandPayload, Throwable throwable) {
        return handler.handle((CommandPayload<S>) commandPayload, (T) throwable);
    }
}
