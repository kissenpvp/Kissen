package net.kissenpvp.api.database;

import org.jspecify.annotations.NonNull;

/**
 * Represents a generic interface for entities that can be persisted and managed inside a database.
 * <p>
 * It provides methods for identifying entities, managing their state, and determining if changes to
 * the entity have yet to be persisted.
 *
 * @param <P> the type of the identifier used for unique identification of the entity
 * @author Ivo Quiring
 */
public interface PersistableEntity<P>
{
    /**
     * Retrieves the unique identifier of this entity.
     *
     * @return the unique identifier of this entity; never null
     */
    @NonNull P id();

    /**
     * Computes a deterministic fingerprint of the entity's current state.
     * Use this value to detect modifications and verify state consistency.
     * <p>
     * Implementations typically derive this value by hashing the entity's relevant attributes.
     *
     * @return an int representing the entity's current-state
     */
    int signature();

    /**
     * Determines whether the entity has changes which have not been saved yet.
     *
     * @return {@code true} if the entity has unsaved changes, {@code false} otherwise
     */
    boolean unsaved();
}
