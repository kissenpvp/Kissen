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
}
