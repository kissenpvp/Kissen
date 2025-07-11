package net.kissenpvp.network.actor.rank;

import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.api.network.actor.rank.Rank;
import net.kissenpvp.api.network.actor.rank.RankSubscription;
import net.kissenpvp.base.KissenCore;
import net.kissenpvp.database.InternalSubscriptionEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * The {@code InternalRankSubscription} class represents an internal implementation of a rank subscription
 * associated with a specific rank and player within the system. It extends the {@code InternalSubscriptionEntity} class
 * and implements the {@code RankSubscription} interface, providing functionality to manage and retrieve
 * data related to rank subscriptions.
 *
 * @author Ivo Quiring
 */
public class InternalRankSubscription extends InternalSubscriptionEntity<String, String, Rank> implements RankSubscription
{
    private final UUID playerId;

    /**
     * Constructs an {@code InternalRankSubscription} instance with the specified {@code id}, {@code parentId}, and {@code playerId}.
     * This constructor initializes an object representing a rank subscription associated with a specific player.
     *
     * @param id       the unique identifier of the rank subscription, must not be null
     * @param parentId the identifier of the parent rank associated with the subscription, must not be null
     * @param playerId the unique identifier of the player associated with the subscription, must not be null
     */
    public InternalRankSubscription(@NotNull String id, @NotNull String parentId, @NotNull UUID playerId)
    {
        super(id, parentId);
        this.playerId = playerId;
    }

    @Override public @NotNull Optional<Rank> parent()
    {
        return Optional.ofNullable(KissenCore.getInstance().rankRepository().find(parentId()).join());
    }

    @Override public int signature()
    {
        return Objects.hash(id(), parentId(), playerId);
    }

    @Override public @NotNull PlayerClient player() throws IllegalStateException
    {
        PlayerClient player = KissenCore.getInstance().playerRepository().find(playerId).join();
        if (Objects.isNull(player))
        {
            String message = "The player with the id %s was not found in the database but is bound to a rank subscription.";
            throw new IllegalStateException(String.format(message, playerId));
        }
        return player;
    }
}
