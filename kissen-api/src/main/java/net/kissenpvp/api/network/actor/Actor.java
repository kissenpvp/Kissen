package net.kissenpvp.api.network.actor;

import net.kissenpvp.api.network.NetworkEntity;
import org.jspecify.annotations.NonNull;

import java.util.Locale;

/**
 * Represents an abstract actor.
 * <p>
 * This interface defines the common properties and behaviors that can be expected from any actor interacting
 * within this networked environment. It extends the {@link NetworkEntity} interface,
 * inheriting its capabilities related to network participation. Implementations
 * of this interface may represent different types of actors such as players or
 * console clients.
 * <p>
 * An AbstractActor provides a username, a locale for language preference,
 * and an operation status (op) to determine if the actor has elevated permissions.
 *
 * @author Ivo Quiring
 */
public interface Actor extends NetworkEntity
{
    /**
     * Retrieves the username of the actor. The username
     * <p>
     * serves as a textual identifier for the actor and is guaranteed to be non-null.
     *
     * @return a non-null string representing the unique username of the actor.
     */
    @NonNull String username();
}
