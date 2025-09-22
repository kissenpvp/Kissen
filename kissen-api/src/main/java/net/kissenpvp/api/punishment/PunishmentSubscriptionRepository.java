package net.kissenpvp.api.punishment;

import net.kissenpvp.api.database.Repository;
import net.kissenpvp.api.network.actor.PlayerClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PunishmentSubscriptionRepository extends Repository<String, PunishmentSubscription>
{
    @NotNull CompletableFuture<@NotNull @UnmodifiableView Collection<PlayerClient>> findTargets(@NotNull UUID linkId);

    @NotNull CompletableFuture<@NotNull @UnmodifiableView Collection<UUID>> findTargetIds(@NotNull UUID linkId);

    @NotNull CompletableFuture<@NotNull @UnmodifiableView Collection<PunishmentSubscription>> findSubscriptions(@NotNull UUID linkId);

    @NotNull CompletableFuture<@NotNull @UnmodifiableView Collection<PunishmentSubscription>> findSubscriptionsByUserId(
            @NotNull UUID userId
    );

}
