package net.kissenpvp.database.keycloak;

import com.google.common.base.Preconditions;
import net.kissenpvp.api.database.CachedRepository;
import net.kissenpvp.api.database.PersistableEntity;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public abstract class KeycloakCachedInternalRepository<P, T extends PersistableEntity<P>> extends KeycloakInternalRepository<P, T> implements CachedRepository<P, T>
{
    private final Map<P, T> cachedEntries;

    public KeycloakCachedInternalRepository()
    {
        this.cachedEntries = new HashMap<>();
    }

    protected abstract @NonNull T[] toCachedEntity(@NonNull String response);

    @Override
    protected @NonNull T[] toEntity(@NonNull String response)
    {
        T[] entity = toCachedEntity(response);
        for(T current : entity) { cachedEntries.put(current.id(), current); }
        return entity;
    }

    @Override
    public boolean cached(@NonNull P id)
    {
        return cachedAll(Collections.singleton(id));
    }

    @Override
    public boolean cachedAll(@NonNull Iterable<P> id)
    {
        for (P currentId : id)
        {
            if (!cachedEntries.containsKey(currentId))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Optional<T>> find(@NonNull P id)
    {
        return find(id, true);
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Collection<T>> findAll(@NonNull Iterable<P> id)
    {
        return findAll(id, true);
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Optional<T>> find(@NonNull P id, boolean utilizeCache)
    {
        if (utilizeCache && cached(id))
        {
            return CompletableFuture.completedFuture(Optional.of(cachedEntries.get(id)));
        }

        return findUncached(id);
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Collection<T>> findAll(@NonNull Iterable<P> id, boolean utilizeCache)
    {
        Preconditions.checkNotNull(id, "The identifier cannot be null.");

        if (!utilizeCache)
        {
            return findAllUncached(id);
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
            return findAllUncached(uncached).thenApply(list ->
            {
                Stream<T> currentlyCached = cached.stream();
                return Stream.concat(list.stream(), currentlyCached).toList();
            });
        }

        return CompletableFuture.completedFuture(Collections.unmodifiableCollection(cached));
    }

    public abstract @NonNull CompletableFuture<@NonNull Optional<T>> findUncached(@NonNull P id);

    public abstract @NonNull CompletableFuture<@NonNull Collection<T>> findAllUncached(@NonNull Iterable<P> id);

    protected  @NonNull Map<P, T> cachedEntries()
    {
        return Map.copyOf(cachedEntries);
    }
}
