package net.kissenpvp.core.api.base;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a functional interface for handling exceptions of a specific type.
 *
 * @param <T> the type of exception to handle
 */
@FunctionalInterface
public interface ExceptionHandler<T extends Throwable> {

    /**
     * Handles the specified exception.
     *
     * @param throwable the exception to handle
     * @return {@code true} if the exception was handled successfully, {@code false} otherwise.
     */
    boolean handle(@NotNull T throwable);

}
