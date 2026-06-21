package net.kissenpvp.api.temporal;

import org.jspecify.annotations.NonNull;

/**
 * Represents an entity that subscribes to a {@link WritableTemporalObject}.
 * <p>
 * This interface extends {@link TemporalSubscriber} and provides functionality
 * for working specifically with writable and mutable temporal objects.
 * <p>
 * By combining temporal subscription capabilities with writable access,
 * implementations can observe and manage temporal data dynamically,
 * enabling adjusting the expiry of a temporal object.
 *
 * @author Ivo Quiring
 */
public interface WritableTemporalSubscriber extends TemporalSubscriber
{

    /**
     * Retrieves the writable temporal object associated with this subscriber.
     * This method provides access to a {@link WritableTemporalObject} that supports
     * mutable expirations and allows interaction with its time-based properties.
     *
     * @return the non-null {@link WritableTemporalObject} associated with the implementing entity
     */
    @Override @NonNull WritableTemporalObject temporal();
}
