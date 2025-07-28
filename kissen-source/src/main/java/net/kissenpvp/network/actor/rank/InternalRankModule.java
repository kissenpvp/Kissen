package net.kissenpvp.network.actor.rank;

import net.kissenpvp.api.database.Repository;
import net.kissenpvp.api.network.actor.rank.Rank;
import net.kissenpvp.api.network.actor.rank.RankModule;
import net.kissenpvp.api.network.actor.rank.RankSubscription;
import org.jetbrains.annotations.NotNull;

public record InternalRankModule(@NotNull Repository<String, Rank> rankRepository, @NotNull Repository<String, RankSubscription> rankSubscriptionRepository) implements RankModule {}
