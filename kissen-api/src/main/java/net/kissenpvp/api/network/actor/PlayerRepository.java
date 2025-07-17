package net.kissenpvp.api.network.actor;

import net.kissenpvp.api.database.Repository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerRepository extends Repository<UUID, PlayerClient>
{
    @NotNull CompletableFuture<@Nullable PlayerClient> findLazily(@NotNull UUID id) throws NullPointerException;
}
