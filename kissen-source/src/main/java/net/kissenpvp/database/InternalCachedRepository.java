package net.kissenpvp.database;

import net.kissenpvp.api.database.CachedRepository;
import net.kissenpvp.api.database.PersistableEntity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * An abstract base class that extends {@link InternalRepository} and introduces a caching mechanism for entities.
 * This class is responsible for managing the persistence and retrieval of entity objects from a database while
 * maintaining
 * an internal cache for performance optimization.
 *
 * @param <P> The type representing the primary key of the entity.
 * @param <T> The type of entity managed by the repository. Must extend {@link PersistableEntity}.
 * @author Ivo Quiring
 */
public abstract class InternalCachedRepository<P, T extends PersistableEntity<P>> extends InternalRepository<P, T> implements CachedRepository<P, T>
{

    private final static Logger log = LoggerFactory.getLogger(InternalCachedRepository.class);
    private final Map<P, T> cachedEntries;

    public InternalCachedRepository(
            @NotNull String table,
            @NotNull Connection connection,
            @NotNull String findQuery,
            @NotNull String findAllQuery,
            @NotNull String findAllByIdQuery
    ) throws NullPointerException
    {
        super(table, connection, findQuery, findAllQuery, findAllByIdQuery);
        this.cachedEntries = new HashMap<>();
    }

    @Override public @NotNull T toEntity(
            @NotNull P id,
            @NotNull ResultSet resultSet
    ) throws SQLException, NullPointerException
    {
        return cache(toCachedEntity(id, resultSet));
    }

    @Override public @NotNull T toEntity(@NotNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        return cache(toCachedEntity(resultSet));
    }

    @Override public @NotNull CompletableFuture<@Nullable T> find(@NotNull P id)
    {
        return find(id, true);
    }

    @Override public @NotNull CompletableFuture<@UnmodifiableView Collection<T>> findAll(@NotNull Iterable<P> id)
    {
        return findAll(id, true);
    }

    @Override public @NotNull CompletableFuture<T> find(@NotNull P id, boolean utilizeCache) throws NullPointerException
    {
        Objects.requireNonNull(id, "The identifier cannot be null.");

        if (utilizeCache && cachedEntries.containsKey(id))
        {
            return CompletableFuture.completedFuture(cachedEntries.get(id));
        }
        return super.find(id);
    }

    @Override public @NotNull CompletableFuture<@UnmodifiableView Collection<T>> findAll(
            @NotNull Iterable<P> id,
            boolean utilizeCache
    ) throws NullPointerException
    {
        Objects.requireNonNull(id, "The identifier cannot be null.");

        if (!utilizeCache)
        {
            return super.findAll(id);
        }

        Collection<T> cached = new ArrayList<>();
        Collection<P> uncached = new ArrayList<>();

        for (P currentId : id)
        {
            if (!cachedEntries.containsKey(currentId))
            {
                uncached.add(currentId);
                continue;
            }
            cached.add(cachedEntries.get(currentId));
        }

        if (!uncached.isEmpty())
        {
            return super.findAll(uncached).thenApply(list ->
            {
                Stream<T> currentlyCached = cached.stream();
                return Stream.concat(list.stream(), currentlyCached).toList();
            });
        }

        return CompletableFuture.completedFuture(Collections.unmodifiableCollection(cached));
    }

    @Override public boolean cached(@NotNull P id) throws NullPointerException
    {
        Objects.requireNonNull(id, "The identifier cannot be null.");

        return cachedEntries.containsKey(id);
    }

    @Override public boolean cachedAll(@NotNull Iterable<P> id) throws NullPointerException
    {
        Objects.requireNonNull(id, "The identifier iterable cannot be null.");

        for (P currentId : id)
        {
            if (cached(currentId))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieves an unmodifiable view of the current cached entries in the repository.
     * The returned map consists of the primary keys and their associated entities.
     *
     * @return An unmodifiable view of the map containing the cached entries, where the keys are of type {@code P}
     * (representing primary keys) and the values are of type {@code T} (representing entities).
     * The returned map is not null and is a snapshot of the current caches state.
     * @see #cached(Object)
     * @see #cachedAll(Iterable)
     */
    protected @UnmodifiableView @NotNull Map<P, T> cachedEntries()
    {
        return Map.copyOf(cachedEntries);
    }

    /**
     * Converts the given database {@link ResultSet} into an entity represented by type {@code T}.
     * The entity is associated with the specified identifier {@code id}.
     *
     * @param id        The unique identifier associated with the entity being created. Must not be null.
     * @param resultSet The {@link ResultSet} containing data retrieved from the database. Must not be null.
     * @return The cached entity object of type {@code T}. This object is constructed based on the provided
     * {@link ResultSet}.
     * @throws SQLException         If an error occurs while processing the {@link ResultSet}.
     * @throws NullPointerException If any of the parameters is null, or if a required value in {@link ResultSet} is
     *                              missing.
     */
    protected abstract @NotNull T toCachedEntity(
            @NotNull P id,
            @NotNull ResultSet resultSet
    ) throws SQLException, NullPointerException;

    /**
     * Converts a {@link ResultSet} retrieved from the database into an entity object of type {@code T}.
     * The returned entity is expected to be stored in an internal cache for future reference.
     *
     * @param resultSet The {@link ResultSet} containing data retrieved from the database. Must not be null.
     * @return The cached entity object of type {@code T}. This object is constructed based on the specified
     * {@link ResultSet}.
     * @throws SQLException         If an SQL error occurs while reading from the {@link ResultSet}.
     * @throws NullPointerException If the provided {@link ResultSet} is null or if a required value is missing.
     */
    protected abstract @NotNull T toCachedEntity(@NotNull ResultSet resultSet) throws SQLException,
            NullPointerException;

    /**
     * Caches the given entity in the repository's internal cache.
     * If an entry with the same ID already exists, it is replaced with the new entity.
     *
     * @param entity The entity to cache. Must not be null.
     * @return The cached entity. The same instance as the input parameter.
     * @throws NullPointerException If the provided entity is null.
     */
    @Contract(pure = true, value = "_ -> _") private @NotNull T cache(@NotNull T entity) throws NullPointerException
    {
        Objects.requireNonNull(entity, "The entity cannot be null.");

        T overridden = cachedEntries.put(entity.id(), entity);
        log.debug("Cached entity: {} (replaced previous: {})", entity, overridden);
        return entity;
    }
}