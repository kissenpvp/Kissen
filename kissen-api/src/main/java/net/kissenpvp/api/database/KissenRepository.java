package net.kissenpvp.api.database;

import com.google.common.base.Preconditions;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public abstract class KissenRepository<P, T extends PersistableEntity<P>> extends SQLExecutor implements Repository<P, T>
{
    public KissenRepository(@NonNull DataSource dataSource) throws NullPointerException
    {
        super(dataSource);
    }

    @Override public @NonNull CompletableFuture<Void> save(@NonNull T id) {
        Preconditions.checkNotNull(id, "The entity cannot be null.");

        return saveAll(Collections.singleton(id));
    }


    /**
     * Computes the size of the given {@link Iterable}. If the {@code Iterable} is an instance
     * of {@link Collection}, its size is retrieved using {@link Collection#size()} for
     * efficiency. Otherwise, the size is calculated by iterating through the elements.
     *
     * @param iterable the {@code Iterable} whose size is to be calculated, must not be null
     * @return the size of the given {@code Iterable} as an integer
     * @throws NullPointerException if the provided {@code Iterable} is null
     */
    protected int computeIterableSize(@NonNull Iterable<?> iterable)
    {
        if(iterable instanceof Collection<?> collection)
        {
            return collection.size();
        }

        int i = 0;
        for (Object ignored : iterable) { i++; }
        return i;
    }

    /**
     * Helper method for validating that a reference is not {@code null}.
     * <p>
     * This behaves like {@link java.util.Objects#requireNonNull(Object)}, but throws a
     * {@link java.util.concurrent.CompletionException} instead. This is useful in
     * asynchronous repository operations where exceptions should be wrapped in
     * {@code CompletionException}.
     *
     * @param <X> the type of the object reference
     * @param obj the object that must not be {@code null}
     * @return the non-null {@code obj}
     * @throws CompletionException if {@code obj} is {@code null}
     */
    protected <X> @NonNull X assumeNotNull(@Nullable X obj) throws CompletionException
    {
        if(Objects.isNull(obj))
        {
            throw new CompletionException(new NullPointerException("The assumed not null object is null."));
        }
        return obj;
    }
}
