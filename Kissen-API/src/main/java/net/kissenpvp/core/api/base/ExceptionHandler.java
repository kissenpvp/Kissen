package net.kissenpvp.core.api.base;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a functional interface for handling exceptions of a specific type.
 * <p>
 * This interface allows for the implementation of a custom handler for exceptions.
 * It can be used in scenarios where specific exception handling logic is required,
 * making your code more robust and maintainable.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * ExceptionHandler<IOException> ioExceptionHandler = ioException -> {
 *     System.err.println("An I/O error occurred: " + ioException.getMessage());
 *     return true;
 * };
 * }</pre>
 *
 * @param <T> the type of exception to handle, which must extend {@link Throwable}
 */
@FunctionalInterface
public interface ExceptionHandler<T extends Throwable> {

    /**
     * Handles the specified exception.
     * <p>
     * This method should contain the logic to manage the given exception of type {@code T}.
     * It returns a boolean indicating whether the exception was handled successfully.
     *
     * <p>Implementations should ensure that they handle the exception appropriately and
     * provide a meaningful return value. A return value of {@code true} indicates that
     * the exception was handled successfully, whereas {@code false} indicates that the
     * handling was unsuccessful or that the exception could not be managed.
     *
     * @param throwable the exception to handle, must not be {@code null}
     * @return {@code true} if the exception was handled successfully, {@code false} otherwise
     * @throws NullPointerException if {@code throwable} is {@code null}
     */
    boolean handle(@NotNull T throwable);

}
