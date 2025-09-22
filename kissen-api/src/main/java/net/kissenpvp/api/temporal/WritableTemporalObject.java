package net.kissenpvp.api.temporal;

import org.jspecify.annotations.NonNull;

import java.time.Instant;
import java.util.Optional;

/**
 * Represents a writable temporal object with mutable expiry behavior.
 * <p>
 * This interface extends {@link TemporalObject} and provides additional capabilities for
 * altering or manipulating the expiry of a temporal object. It is useful in scenarios
 * where temporal objects need the ability to track changes to their expiry or manage
 * expiration dynamically.
 *
 * @author Ivo Quiring
 */
public interface WritableTemporalObject extends TemporalObject
{
    /**
     * Marks the temporal object as expired.
     * <p>
     * This method updates the state or metadata of the corresponding temporal object
     * to indicate that it is no longer valid or active. Once expired, the object may
     * represent a completed or irreversibly altered state.
     */
    void expire();

    /**
     * Retrieves the expected expiry time of the temporal object, if available.
     * <p>
     * This value represents the intended or predicted expiration time of the object,
     * which may differ from the actual expiry time depending on modifications
     * or status changes.
     *
     * @return an {@link Optional} containing the expected expiry time as an {@link Instant},
     * or an empty {@link Optional} if the expected expiry time is not defined
     */
    @NonNull Optional<Instant> expectedExpiry();

    /**
     * Checks if the expiry of the temporal object has been altered.
     * <p>
     * This method indicates whether the expiry time or status of the temporal object
     * has been changed after its initial configuration or default state.
     *
     * @return true if the expiry has been altered, false otherwise
     */
    boolean expiryAltered();
}
