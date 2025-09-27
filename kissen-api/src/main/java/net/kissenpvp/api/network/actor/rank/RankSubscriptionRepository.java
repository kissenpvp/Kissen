package net.kissenpvp.api.network.actor.rank;

import net.kissenpvp.api.database.Repository;
import net.kissenpvp.api.network.actor.PlayerClient;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface RankSubscriptionRepository extends Repository<String, RankSubscription>
{
    @NonNull CompletableFuture<@NonNull Optional<RankSubscription>> findActive(@NonNull PlayerClient player);

    @NonNull CompletableFuture<@NonNull List<RankSubscription>> findHistory(@NonNull PlayerClient player);

}
