package net.kissenpvp.api.network.actor.rank;

import net.kissenpvp.api.database.SubscriptionEntity;
import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.api.temporal.WritableTemporalSubscriber;
import org.jspecify.annotations.NonNull;

/**
 * Represents a subscription to a rank within the system, defining the relationship between a player
 * and a specific rank.
 * <p>
 * This subscription links a player client to a rank entity, enabling access
 * to rank-related properties and attributes.
 * <p>
 * Extends the {@link SubscriptionEntity} interface, inheriting functionalities to manage
 * relationships between parent and child entities.
 *
 * @author Ivo Quiring
 */
public interface RankSubscription extends SubscriptionEntity<String, String, Rank>, WritableTemporalSubscriber
{
    /**
     * Retrieves the player client associated with this rank subscription.
     * <p>
     * This method establishes a link between the subscription and a specific player, enabling access to
     * player-related data and attributes.
     *
     * @return a non-null {@link PlayerClient} instance representing the player associated with this subscription
     * @throws IllegalStateException if the player client cannot be determined, or if the subscription is in an
     *                               invalid state
     */
    @NonNull PlayerClient player() throws IllegalStateException;
}
