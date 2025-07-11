package net.kissenpvp.api.network.actor.rank;

import net.kissenpvp.api.database.SubscriptionEntity;
import net.kissenpvp.api.network.actor.PlayerClient;
import org.jetbrains.annotations.NotNull;

public interface RankSubscription extends SubscriptionEntity<String, String, Rank>
{
    @NotNull PlayerClient player() throws IllegalStateException;
}
