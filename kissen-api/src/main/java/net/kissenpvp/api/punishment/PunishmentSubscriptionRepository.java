package net.kissenpvp.api.punishment;

import net.kissenpvp.api.database.Repository;
import net.kissenpvp.api.network.actor.PlayerClient;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PunishmentSubscriptionRepository extends Repository<String, PunishmentSubscription>
{
    @NonNull CompletableFuture<@NonNull  Collection<PlayerClient>> findTargets(@NonNull UUID linkId);

    @NonNull CompletableFuture<@NonNull  Collection<UUID>> findTargetIds(@NonNull UUID linkId);

    @NonNull CompletableFuture<@NonNull  Collection<PunishmentSubscription>> findSubscriptions(@NonNull UUID linkId);

    @NonNull CompletableFuture<@NonNull  Collection<PunishmentSubscription>> findSubscriptionsByUserId(@NonNull UUID userId);

}
