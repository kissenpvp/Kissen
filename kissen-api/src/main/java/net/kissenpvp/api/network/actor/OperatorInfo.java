package net.kissenpvp.api.network.actor;

import net.kissenpvp.api.database.PersistableEntity;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

/**
 * Represents an operator with identifiable properties and privileges within the system.
 * <p>
 * This entity provides methods to access the operator's name, operational level, and privilege
 * to bypass player limits.
 *
 * @author Ivo Quiring
 */
public interface OperatorInfo extends PersistableEntity<UUID>
{
    /**
     * Retrieves the name of the operator.
     *
     * @return a non-null string representing the operator's name.
     */
    @NonNull String name();

    /**
     * Retrieves the operational level of the operator.
     *
     * @return an integer representing the operator's level, typically used to determine authority or privilege
     * within the system.
     */
    int getLevel();

    /**
     * Determines whether the operator has the privilege to bypass the player limit.
     *
     * @return true if the operator can bypass the player limit, false otherwise.
     */
    boolean getBypassesPlayerLimit();
}
