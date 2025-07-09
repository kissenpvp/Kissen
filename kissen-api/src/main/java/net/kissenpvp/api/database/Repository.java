package net.kissenpvp.api.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a generic repository interface responsible for performing CRUD
 * operations and entity management in an asynchronous manner. This interface
 * supports retrieving, saving, and verifying entities within the repository
 * while ensuring thread safety and immutability for returned collections.
 *
 * @param <P> the type representing the identifier of the entities
 * @param <T> the type of the entities being managed, which must extend {@link PersistableEntity}
 */
public interface Repository<P, T extends PersistableEntity<P>>
{
    /**
     * Asynchronously retrieves an entity from the repository based on the specified identifier.
     *
     * @param id the identifier of the entity to be retrieved; must not be null
     * @return a {@link CompletableFuture} that completes with the entity corresponding to the provided identifier; never null
     * @throws NullPointerException if the provided identifier is null
     */
    @NotNull CompletableFuture<T> find(@NotNull P id) throws NullPointerException;

    /**
     * Retrieves a collection of entities corresponding to the specified identifiers asynchronously.
     * The returned collection is unmodifiable, ensuring that it cannot be altered after retrieval.
     *
     * @param id an iterable containing the identifiers of the entities to retrieve; must not be null
     * @return a {@link CompletableFuture} that completes with an unmodifiable view of
     *         the collection of entities corresponding to the provided identifiers; never null
     * @throws NullPointerException if the given iterable or any of its elements are null
     */
    @NotNull CompletableFuture<@UnmodifiableView Collection<T>> findAll(@NotNull Iterable<P> id)  throws NullPointerException;

    /**
     * Retrieves all entities from the repository asynchronously.
     * The entities are returned as a collection that is unmodifiable, ensuring that
     * the collection cannot be altered after retrieval.
     *
     * @return a {@link CompletableFuture} that completes with an unmodifiable view of
     *         the collection of all entities in the repository; never null
     */
    @NotNull CompletableFuture<@UnmodifiableView Collection<T>> findAll();

    /**
     * Checks if an entity with the specified identifier exists in the repository.
     * This method is asynchronous and returns a {@link CompletableFuture} that
     * completes with a boolean indicating the existence of the entity.
     *
     * @param id the identifier of the entity to check; must not be null
     * @return a {@link CompletableFuture} that completes with {@code true} if the entity exists,
     *         or {@code false} otherwise
     * @throws NullPointerException if the provided identifier is null
     */
    @NotNull CompletableFuture<Boolean> has(@NotNull P id)  throws NullPointerException;

    /**
     * Persists the given entity to the underlying storage asynchronously.
     * This method ensures the entity is saved successfully.
     *
     * @param id The entity to be saved must not be null
     * @return a {@link CompletableFuture} that completes with {@code null} when the entity is persisted successfully
     * @throws NullPointerException if the provided entity is null
     */
    @NotNull CompletableFuture<Void> save(@NotNull T id)  throws NullPointerException;

    /**
     * Persists all the provided entities to the underlying storage asynchronously.
     * This method ensures that each entity in the given iterable is saved successfully.
     *
     * @param id an iterable containing the entities to be saved must not be null
     * @return a {@link CompletableFuture} that completes with {@code null} when all entities are persisted successfully
     * @throws NullPointerException if the provided iterable or any of its elements are null
     */
    @NotNull CompletableFuture<Void> saveAll(@NotNull Iterable<T> id) throws NullPointerException;

    /**
     * Retrieves the name of the table associated with this repository.
     * The table name is used to perform operations on the underlying
     * database, such as queries and updates.
     *
     * @return the name of the table as a non-null string
     */
    @NotNull String table();
}
