package net.kissenpvp.core.api.time;

import net.kissenpvp.core.api.event.EventCancelledException;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * TemporalObject is an interface that deals with entities having temporal aspects.
 * It holds the start time, duration and the eventual end time of any temporal event.
 */
public interface TemporalObject {

    /**
     * Gets the start time of the temporal event.
     * @return The start time of the event. This is of Instant type and is guaranteed to be non-null.
     */
    @NotNull Instant getStart();

    /**
     * Retrieves the duration of the temporal event.
     * Duration is calculated from the start time till the end of the event.
     * @return The duration of the event wrapped in an Optional. This could be empty if duration is not known.
     */
    @NotNull Optional<AccurateDuration> getAccurateDuration();

    /**
     * Retrieves the end time of the temporal event.
     * @return The end time of the event. This could be empty if the end time is not yet known.
     */
    @NotNull Optional<Instant> getEnd();

    /**
     * Sets the end time of the temporal event.
     * @param end The Instant at which the event ends. This could be null.
     * @throws EventCancelledException If the supplied end time leads to a cancellation or invalidation of the event.
     */
    void setEnd(@Nullable Instant end) throws EventCancelledException;

    /**
     * Retrieves the predicted end time of the temporal event.
     * @return The predicted end time wrapped in an Optional. Could be empty if the end time is not predicted.
     */
    @NotNull Optional<Instant> getPredictedEnd();

    /**
     * Check whether the temporal event is still valid.
     * <p>
     * This method checks the current time and returns whether the temporal object is still valid.
     *
     * @return true if the event is valid (i.e., it has a start time and either a duration or an end time), false otherwise.
     */
    boolean isValid();

    @NotNull Component endComponent(@NotNull DateTimeFormatter formatter);
}
