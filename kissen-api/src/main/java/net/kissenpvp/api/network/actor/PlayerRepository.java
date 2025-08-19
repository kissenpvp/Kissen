package net.kissenpvp.api.network.actor;

import net.kissenpvp.api.database.CachedRepository;
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
public interface PlayerRepository extends CachedRepository<UUID, PlayerClient>
{
    boolean cached(@NotNull String name) throws NullPointerException;
}
