package net.kissenpvp.api.network.actor;

import net.kissenpvp.api.database.CachedRepository;
import net.kissenpvp.api.database.Repository;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;


import java.util.Collection;
import java.util.Optional;
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

    /**
     * Retrieves a {@link PlayerClient} by their unique name asynchronously.
     * <p>
     * This method searches for a player client in the repository using the provided name
     * and returns a {@link CompletableFuture} of the result. If no player matches the
     * given name, the result will be {@code null}.
     * <p>
     * This is a convenience overload that defaults to calling
     * {@link #findByName(String, boolean) findByName(name, true)}, and by default enables cache utilization.
     *
     * @param name the unique name of the player client to be retrieved; must not be {@code null}.
     * @return a {@link CompletableFuture} containing the {@link PlayerClient} associated with the given name,
     * or {@code null} if no such player exists.
     * @throws NullPointerException if the provided name is {@code null}.
     * @see #findByName(String, boolean)
     */
    @NonNull CompletableFuture<@NonNull Optional<PlayerClient>> findByName(@NonNull String name) throws NullPointerException;

    @NonNull CompletableFuture<@NonNull Optional<UUID>> findLinkId(@NonNull UUID uuid) throws NullPointerException;

    /**
     * Retrieves a {@link PlayerClient} by their unique name asynchronously.
     * <p>
     * This method searches for a player client in the repository using the provided name.
     * The operation can optionally use caching for improved performance. If no player
     * matches the given name, the result will be {@code null}.
     *
     * @param name         the unique name of the player client to be retrieved; must not be {@code null}.
     * @param utilizeCache whether to use cached values during the lookup operation.
     * @return a {@link CompletableFuture} containing the {@link PlayerClient} associated with the given name,
     * or {@code null} if no such player exists.
     * @throws NullPointerException if the provided name is {@code null}.
     */
    @NonNull CompletableFuture<@NonNull Optional<PlayerClient>> findByName(@NonNull String name, boolean utilizeCache) throws NullPointerException;

    /**
     * Retrieves a collection of {@link PlayerClient} entities based on the provided iterable of player names.
     * <p>
     * This method performs an asynchronous search for all {@link PlayerClient} instances whose names match the
     * provided list and returns a result wrapped in a {@link CompletableFuture}. The returned collection is
     * unmodifiable to ensure the integrity of the results.
     * <p>
     * This is a convenience overload that defaults to calling
     * {@link #findAllByName(Iterable, boolean) findAllByName(name, true)}, and by default enables cache utilization.
     *
     * @param name iterable of player names to search for; must not be {@code null} and must not contain {@code null}
     *             elements.
     * @return a {@link CompletableFuture} containing an unmodifiable view of a collection of {@link PlayerClient}
     * instances matching the provided names; if no matches are found, the collection will be empty.
     * @throws NullPointerException if the provided iterable or any of its elements are {@code null}.
     * @see #findAllByName(Iterable, boolean)
     */
    @NonNull CompletableFuture< Collection<PlayerClient>> findAllByName(@NonNull Iterable<String> name) throws NullPointerException;

    /**
     * Performs an asynchronous search for all {@link PlayerClient} instances
     * whose names match the given iterable of names.
     * <p>
     * This method allows optional caching during the operation to improve performance.
     * The resulting collection is unmodifiable and may be empty if no matches are found.
     *
     * @param name         an iterable collection of player names to search for; must not be {@code null} and must
     *                     not contain {@code null} elements.
     * @param utilizeCache whether to use cache during the lookup operation.
     * @return a {@link CompletableFuture} that resolves to an unmodifiable view of the collection of
     * {@link PlayerClient} instances
     * matching the provided names; the collection will be empty if no matches are found.
     * @throws NullPointerException if the provided iterable is {@code null} or contains {@code null} elements.
     */
    @NonNull CompletableFuture< Collection<PlayerClient>> findAllByName(@NonNull Iterable<String> name, boolean utilizeCache) throws NullPointerException;

    /**
     * Determines if the data associated with the specified player's name is currently cached.
     * <p>
     * This method checks the repository cache for the existence of a record corresponding
     * to the given name.
     *
     * @param name the unique name of the player to check in the cache; must not be {@code null}.
     * @return {@code true} if the player's data is cached; {@code false} otherwise.
     * @throws NullPointerException if the provided name is {@code null}.
     */
    boolean cached(@NonNull String name) throws NullPointerException;
}
