package net.kissenpvp.api.database;

import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * An extension of the {@link Repository} interface that adds in caching capabilities
 * to enhance performance by reducing repetitive database interactions.
 * <p>
 * This interface allows for selective utilization of cache during entity access
 * and provides methods to check the cache status of specific entities or collections.
 *
 * @param <P> the type representing the unique identifier of the entities
 * @param <T> the type of the entities being managed, which must extend {@link PersistableEntity}
 * @author Ivo Quiring
 */
public interface CachedRepository<P, T extends PersistableEntity<P>> extends Repository<P, T>
{

    /**
     * Finds and retrieves an entity asynchronously from the repository using the specified identifier.
     * <p>
     * This method uses caching based on the provide flag to optimize performance.
     * If no entity is associated with the provided identifier, the result will be {@code null}.
     *
     * @param id           the identifier of the entity to find; must not be null
     * @param utilizeCache a boolean indicating whether to use the cache when retrieving the entity
     * @return a {@link CompletableFuture} that completes with the entity of type {@code T};
     * never null, but may complete with {@code null} if no entity is found
     * @throws NullPointerException if the provided identifier is null
     * @see #findAll(Iterable, boolean)
     */
    @NonNull CompletableFuture<@NonNull Optional<T>> find(@NonNull P id, boolean utilizeCache);

    /**
     * Retrieves a collection of entities corresponding to the specified identifiers asynchronously.
     * <p>
     * This method provides the option to use a caching mechanism to enhance performance.
     * The returned collection is unmodifiable, making it unalterable after retrieval.
     *
     * @param id           iterable containing the identifiers of the entities to retrieve; must not be null
     * @param utilizeCache a boolean indicating whether to use the cache when retrieving the entities
     * @return a {@link CompletableFuture} that completes with an unmodifiable view of
     * entities corresponding to the provided identifiers; never null
     * but may complete with an empty collection if no entities are found
     * @throws NullPointerException if the provided iterable or any of its elements are null
     * @see #find(Object, boolean)
     */
    @NonNull CompletableFuture<@NonNull Collection<T>> findAll(@NonNull Iterable<P> id, boolean utilizeCache);

    /**
     * Checks whether the entity associated with the specified identifier is currently cached.
     *
     * @param id the identifier of the entity to check; must not be null
     * @return {@code true} if the entity is cached, {@code false} otherwise
     * @throws NullPointerException if the provided identifier is null
     * @see #cachedAll(Iterable)
     */
    boolean cached(@NonNull P id);

    /**
     * Checks whether all entities associated with the specified identifiers are currently cached.
     *
     * @param id iterable containing the identifiers of the entities to check; must not be null
     * @return {@code true} if all entities are cached, {@code false} otherwise
     * @throws NullPointerException if the provided iterable or any of its elements are null
     * @see #cached(Object)
     */
    boolean cachedAll(@NonNull Iterable<P> id);

}
