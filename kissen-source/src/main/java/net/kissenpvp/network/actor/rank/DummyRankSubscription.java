package net.kissenpvp.network.actor.rank;

import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.api.network.actor.rank.DefaultSubscription;
import net.kissenpvp.api.network.actor.rank.Rank;
import net.kissenpvp.api.temporal.WritableTemporalObject;
import net.kissenpvp.temporal.InternalWritableTemporalObject;
import net.kissenpvp.temporal.timespan.PermanentTimeSpan;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class DummyRankSubscription implements DefaultSubscription
{
    private static final Rank DUMMY_RANK = new DummyRank();

    private final PlayerClient playerClient;

    public DummyRankSubscription(PlayerClient playerClient)
    {
        this.playerClient = playerClient;
    }

    @Override public @NonNull PlayerClient player() throws IllegalStateException
    {
        return playerClient;
    }

    @Override public @NonNull Optional<Rank> parent()
    {
        return Optional.of(DUMMY_RANK);
    }

    @Override public @NonNull String parentId()
    {
        return "dummy";
    }

    @Override public @NonNull String id()
    {
        return "dummy";
    }

    @Override public @NonNull WritableTemporalObject temporal()
    {
        return InternalWritableTemporalObject.toTemporal(new PermanentTimeSpan());
    }
}
