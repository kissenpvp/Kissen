package net.kissenpvp.api.temporal;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Optional;

/**
 * Represents a temporal object with a defined start time and an optional expiry time.
 * <p>
 * This interface allows querying of temporal intervals, with the ability to retrieve the
 * start time and an optional expiry time if set. Implementations of this interface might
 * represent concepts such as events, time spans, or other temporal abstractions.
 *
 * @author Ivo Quiring
 */
public interface TemporalObject
{
    /**
     * Retrieves the expiry time of the temporal object, if available.
     * <p>
     * This method returns an {@link Optional} containing the expiration time of
     * the temporal object as an {@link Instant}. If the expiry time has not been
     * set or is undefined, an empty {@link Optional} is returned.
     *
     * @return an {@link Optional} containing the expiry time as an {@link Instant},
     * or an empty {@link Optional} if the expiry time is not defined
     */
    @NotNull Optional<Instant> expiry();

    boolean hasExpiry();

    /**
     * Retrieves the start time of the temporal object.
     * <p>
     * This method returns a non-null {@link Instant} representing the start time
     * associated with the temporal object. The start time defines the moment the
     * temporal object begins or comes into effect.
     *
     * @return a non-null {@link Instant} representing the start time of the temporal object
     */
    @NotNull Instant start();
}
