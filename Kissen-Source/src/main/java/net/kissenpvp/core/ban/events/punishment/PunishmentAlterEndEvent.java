package net.kissenpvp.core.ban.events.punishment;

import lombok.Setter;
import net.kissenpvp.core.api.ban.AbstractPunishment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Optional;

/**
 * The {@link PunishmentAlterEndEvent} class extends the {@link PunishmentEvent} class, adding functionality for managing change events related to the ending time of a punishment.
 * This class introduces new properties to represent the previous and the new ending time of the punishment.
 * <p>
 * A {@link PunishmentAlterEndEvent} is triggered when the end of a punishment is changed.
 * This class encapsulates that action by keeping references to the punishment's previous end and updated end.
 * <p>
 * This class retains all the functionality of the {@link PunishmentEvent} class and extends it by providing access to the previous and updated end of the punishment.
 * This class has a {@link Setter} annotation, indicating that setter methods are autogenerated by the Lombok library for the 'updatedEnd' field.
 *
 * @param <P> The type of punishment managed by this event, this type extends {@link AbstractPunishment}
 *
 * @see PunishmentEvent
 * @see Instant
 * @see Setter
 */
@Setter
public class PunishmentAlterEndEvent<P extends AbstractPunishment<?>> extends PunishmentEvent<P> {

    private final Instant previousEnd;
    private Instant updatedEnd;

    /**
     * Constructs a new {@link PunishmentAlterEndEvent} with the specified {@link AbstractPunishment}, previous ending time and new ending time.
     * This constructor initializes the PunishmentAlterEndEvent by calling its superclass's constructor with the given punishment and
     * sets the 'previousEnd' and 'updatedEnd' properties to the given values.
     * <p>
     * This constructor should be used when a punishment's end time has changed.
     *
     * @param punishment the punishment this event relates to, cannot be null.
     * @param previousEnd the previous end time of the punishment, can be null.
     * @param updatedEnd the new end time of the punishment, can be null.
     *
     * @see PunishmentAlterEndEvent
     * @see AbstractPunishment
     * @see Instant
     * @see NotNull
     * @see Nullable
     */
    public PunishmentAlterEndEvent(@NotNull P punishment, @Nullable Instant previousEnd, @Nullable Instant updatedEnd) {
        super(punishment);
        this.previousEnd = previousEnd;
        this.updatedEnd = updatedEnd;
    }

    /**
     * Returns an {@link Optional} containing the previous end of the punishment.
     * If the previousEnd is null, the Optional will be empty.
     *
     * @return an Optional containing the previous end of the punishment.
     *
     * @see Optional
     * @see Instant
     * @see NotNull
     */
    public @NotNull Optional<Instant> getPreviousEnd() {
        return Optional.ofNullable(previousEnd);
    }

    /**
     * Returns an {@link Optional} containing the updated end of the punishment.
     * If the updatedEnd is null, the Optional will be empty.
     *
     * @return an Optional containing the updated end of the punishment.
     *
     * @see Optional
     * @see Instant
     * @see NotNull
     */
    public @NotNull Optional<Instant> getUpdatedEnd() {
        return Optional.ofNullable(updatedEnd);
    }
}
