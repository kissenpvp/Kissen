package net.kissenpvp.api.network.actor;

import net.kissenpvp.api.database.PersistableEntity;
import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentSubscription;
import net.kissenpvp.api.temporal.timespan.DefinedTimeSpan;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a player client.
 * <p>
 * It is uniquely identified by its {@code id}
 * as defined by {@link PersistableEntity} and includes additional details.
 * <p>
 * This interface extends {@link Actor} to inherit common network actor properties
 * (e.g., username and locale) and {@link PersistableEntity} to support database persistence.
 *
 * @author Ivo Quiring
 */
public interface PlayerClient extends Actor, PersistableEntity<UUID>
{
    /**
     * Returns the stable identifier that associates this player client with an external identity
     * or a group of linked accounts. This value is guaranteed to be non-null.
     * <p>
     * It is used to correlate multiple accounts belonging to the same user, for example, to
     * apply cross-account punishments or manage account linking.
     *
     * @return a non-null {@link UUID} representing the link identifier for this player client.
     */
    @NotNull UUID linkId();

    /**
     * Retrieves the timestamp of the player's most recent login. This value represents
     * the moment when the player last accessed the system and is guaranteed to be non-null.
     *
     * @return a non-null {@link Instant} representing the timestamp of the player's last login.
     */
    @NotNull Instant lastLogin();

    /**
     * Retrieves the total amount of time the player has actively spent in the server.
     * This is represented by a {@link DefinedTimeSpan}, encapsulating the duration
     * and period of time played.
     *
     * @return a non-null {@link DefinedTimeSpan} representing the player's total active time in the system.
     */
    @NotNull DefinedTimeSpan timePlayed();

    @NotNull PunishmentSubscription punish(@NotNull Punishment punishment) throws NullPointerException;

    @NotNull PunishmentSubscription punish(@NotNull Punishment punishment, @Nullable Component message) throws NullPointerException;
}
