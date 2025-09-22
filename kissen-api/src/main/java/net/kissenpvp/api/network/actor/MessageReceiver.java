package net.kissenpvp.api.network.actor;

import net.kyori.adventure.audience.Audience;
import org.jspecify.annotations.NonNull;

/**
 * Represents an entity capable of receiving.
 * <p>
 * This is a marker interface
 * that provides an abstraction for components that can handle audience interactions through the
 * methods defined in the {@link Audience} from net kyori's adventure api.
 * <p>
 * Implementations of this interface may include various network actors capable of receiving
 * and processing messages, such as Console clients or player clients within the networked environment.
 *
 * @author Ivo Quiring
 */
public interface MessageReceiver
{
    /**
     * Retrieves the {@link Audience} instance associated with this message receiver.
     * The {@link Audience} provides mechanisms for interacting with and sending
     * formatted messages to the entity represented by this receiver.
     *
     * @return a non-null {@link Audience} representing the communication interface for this receiver.
     */
    @NonNull Audience audience();
}
