package net.kissenpvp.api.temporal;

import org.jspecify.annotations.NonNull;

/**
 * Represents an entity that subscribes to or is associated with a {@link TemporalObject}.
 * <p>
 * Temporal subscriber implementations are expected to provide access to a
 * {@code TemporalObject}, which contains temporal properties such as
 * start time and optional expiry.
 * <p>
 * This interface is useful for associating temporal behaviors or characteristics
 * with domain objects, enabling them to interact with or observe temporal data.
 *
 * @author Ivo Quiring
 */
public interface TemporalSubscriber
{
    /**
     * Provides access to the associated {@link TemporalObject}, which contains
     * temporal properties such as start time and optional expiry time.
     *
     * @return the non-null {@link TemporalObject} associated with the implementing entity
     */
    @NonNull TemporalObject temporal();
}
