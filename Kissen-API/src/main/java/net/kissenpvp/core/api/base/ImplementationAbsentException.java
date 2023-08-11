package net.kissenpvp.core.api.base;

/**
 * Signals that an implementation is absent for an interface file.
 * This exception is thrown when an interface file exists but does not have a child class extending it
 * and defining the necessary code properties.
 *
 * <p>This exception is a subclass of {@code IllegalStateException}, indicating that the program's
 * current state is illegal due to the absence of a required implementation for an interface.
 *
 * <p>Typically, this exception is thrown when attempting to use a specific interface that should have
 * an implementation class associated with it, but no such implementation is found.
 *
 * <p>Example usage:
 * <pre>{@code
 * try {
 *     getImplementation(SomeInterface.class);
 * } catch (ImplementationAbsentException e) {
 *     System.err.println("Error: The implementation for SomeInterface is missing.");
 * }
 * }</pre>
 */

public class ImplementationAbsentException extends IllegalStateException {
}
