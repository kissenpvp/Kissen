package net.kissenpvp.network.actor.rank;

import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.api.network.actor.rank.Rank;
import net.kissenpvp.api.network.actor.rank.RankSubscription;
import net.kissenpvp.database.InternalSubscriptionEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class InternalRankSubscription extends InternalSubscriptionEntity<String, String, Rank> implements RankSubscription
{
    private final UUID playerId;

    public InternalRankSubscription(@NotNull String id, @NotNull String parentId, @NotNull UUID playerId)
    {
        super(id, parentId);
        this.playerId = playerId;
    }

    @Override public @NotNull Optional<Rank> parent()
    {
        return Optional.empty(); //TODO
    }

    @Override public int signature()
    {
        return Objects.hash(id(), parentId(), playerId);
    }

    @Override public @NotNull PlayerClient player()
    {
        return null; //TODO
    }
}
