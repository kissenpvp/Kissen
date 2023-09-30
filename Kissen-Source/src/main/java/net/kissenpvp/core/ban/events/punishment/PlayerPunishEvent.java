package net.kissenpvp.core.ban.events.punishment;

import net.kissenpvp.core.api.ban.Punishment;
import org.jetbrains.annotations.NotNull;

/**
 * The {@link PlayerPunishEvent} class extends the {@link PunishmentEvent} class, adding functionality for managing events where a player-specific punishment is enacted.
 * This class does not introduce any new properties or methods but specifies player-specific punishment events.
 * <p>
 * A {@link PlayerPunishEvent} is triggered when a punishment of type P is enacted on a player. This class encapsulates that action.
 * <p>
 * This class retains all the functionality of the {@link PunishmentEvent} class and should be used whenever a player-specific punishment event needs to be created or processed.
 *
 * @param <P> The specific type of punishment managed by this event, this type extends {@link Punishment}
 *
 * @see PunishmentEvent
 * @see Punishment
 * @see NotNull
 */
public class PlayerPunishEvent<P extends Punishment<?>> extends PunishmentEvent<P> {

    /**
     * Constructs a new {@link PlayerPunishEvent} with the specified {@link Punishment}.
     * This constructor initializes the PunishmentEvent by calling its superclass's constructor with the given punishment.
     * <p>
     * This constructor should be used when a player-specific punishment is enacted.
     *
     * @param punishment the punishment related to this event, cannot be null.
     *
     * @see PlayerPunishEvent
     * @see Punishment
     * @see NotNull
     */
    public PlayerPunishEvent(@NotNull P punishment) {
        super(punishment);
    }
}
