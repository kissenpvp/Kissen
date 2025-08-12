package net.kissenpvp.network.actor.rank;

import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.api.network.actor.PlayerModule;
import net.kissenpvp.api.network.actor.rank.Rank;
import net.kissenpvp.api.network.actor.rank.RankModule;
import net.kissenpvp.api.network.actor.rank.RankSubscription;
import net.kissenpvp.api.temporal.WritableTemporalObject;
import net.kissenpvp.base.KissenCore;
import net.kissenpvp.database.InternalSubscriptionEntity;
import net.kissenpvp.temporal.InternalWritableTemporalObject;
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
    private final WritableTemporalObject temporalObject;

    public InternalRankSubscription(@NotNull String id, @NotNull String parentId, @NotNull UUID playerId) throws NullPointerException {
        this(id, parentId, playerId, new InternalWritableTemporalObject());
    }

    public InternalRankSubscription(@NotNull String id, @NotNull String parentId, @NotNull UUID playerId, @NotNull WritableTemporalObject temporalObject) throws NullPointerException
    {
        super(id, parentId);

        Objects.requireNonNull(playerId, "The player id cannot be null.");

        this.playerId = playerId;
        this.temporalObject = temporalObject;
    }

    @Override public @NotNull Optional<Rank> parent()
    {
        RankModule module = KissenCore.getInstance().rankModule();
        return Optional.ofNullable(module.rankRepository().find(parentId()).join());
    }

    @Override public int signature()
    {
        return Objects.hash(id(), parentId(), playerId);
    }

    @Override public @NotNull PlayerClient player() throws IllegalStateException
    {
        PlayerModule module = KissenCore.getInstance().playerModule();
        PlayerClient player = module.playerRepository().find(playerId).join();
        if (Objects.isNull(player))
        {
            String message = "The player with the id %s was not found in the database but is bound to a rank subscription.";
            throw new IllegalStateException(String.format(message, playerId));
        }
        return player;
    }

    @Override
    public @NotNull WritableTemporalObject temporal() {
        return temporalObject;
    }
}
