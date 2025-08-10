package net.kissenpvp.api.network.actor;

import net.kissenpvp.api.database.Repository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Defines a repository for managing player clients, with support for asynchronous operations.
 * <p>
 * This interface provides methods to retrieve and manage {@link PlayerClient} instances
 * in a thread-safe and non-blocking manner.
 * <p>
 * Extends the {@link Repository} interface, which provides base functionality for CRUD
 * operations and asynchronous entity management. The primary key for the players
 * is represented by {@link UUID}, and the entities managed by the repository are
 * of the type {@link PlayerClient}.
 *
 * @author Ivo Quiring
 */
public interface PlayerRepository extends Repository<UUID, PlayerClient>
{
    /**
     * Retrieves a {@link PlayerClient} instance lazily based on the provided unique identifier.
     * <p>
     * This method executes asynchronously and returns a {@link CompletableFuture} that will
     * complete with the corresponding {@link PlayerClient}, or {@code null} if no player is found
     * for the provided identifier.
     *
     * @param id the unique {@link UUID} identifying the {@link PlayerClient} to be retrieved; must not be null
     * @return a {@link CompletableFuture} that completes with the {@link PlayerClient} instance if found, or {@code null} if no player is associated with the given {@link UUID}
     * @throws NullPointerException if {@code id} is null
     */
    @NotNull CompletableFuture<@Nullable PlayerClient> findLazily(@NotNull UUID id) throws NullPointerException;
}
