package net.kissenpvp.network.actor;

import net.kissenpvp.api.network.actor.rank.Rank;
import net.kissenpvp.api.temporal.timespan.DefinedTimeSpan;
import net.kissenpvp.network.actor.rank.DummyRank;
import net.kissenpvp.temporal.timespan.InternalDefinedTimeSpan;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Represents actor data, containing rank information and a defined time span.
 * <p>
 * This class provides functionality to calculate time played, determine the most
 * appropriate rank for an actor, and retrieve associated ranks.
 *
 * @author Ivo Quiring
 */
public record ActorData(@NotNull List<Rank> ranks, @NotNull DefinedTimeSpan definedTimeSpan)
{
    /**
     * Calculates the total time played by a player from the given last join time to the current time,
     * including an existing defined time span associated with the actor.
     *
     * @param lastJoin the {@link Instant} representing the last join time of the player, must not be null
     * @return a {@link DefinedTimeSpan} representing the total calculated time played
     */
    public @NotNull DefinedTimeSpan calculateTimePlayed(@NotNull Instant lastJoin)
    {
        Duration totalTimePlayed = Duration.between(lastJoin, Instant.now()).plus(Duration.from(definedTimeSpan));
        return new InternalDefinedTimeSpan(totalTimePlayed.get(ChronoUnit.MILLIS));
    }

    /**
     * Determines the most appropriate rank for an actor based on the list of ranks,
     * taking into account rank expiration.
     * <p>
     * The method iterates through the ranks in reverse order (from highest to lowest precedence).
     * It checks the expiration time of each rank. If the rank is expired (i.e., its expiry time is
     * present and occurs before the current time), it skips that rank. The first non-expired rank is returned.
     * <p>
     * If no valid rank is found, a default dummy rank is returned.
     *
     * @return the most suitable {@link Rank} for the actor, or a dummy rank if none are applicable
     */
    public @NotNull Rank calculateRank()
    {
        for (Rank rank : ranks.reversed())
        {
            Optional<Instant> expiry = rank.temporal().expiry();
            if (expiry.isPresent() && expiry.get().isBefore(Instant.now()))
            {
                continue;
            }

            return rank;
        }

        // in this case no rank has been found, it should now default to the dummy rank
        return new DummyRank();
    }

    /**
     * Retrieves an unmodifiable view of the list of ranks associated with the actor.
     *
     * @return a {@link List} containing {@link Rank} objects representing the ranks of the actor,
     * guaranteed to be unmodifiable and non-null
     */
    @Override public @NotNull @UnmodifiableView List<Rank> ranks()
    {
        return Collections.unmodifiableList(ranks);
    }
}
