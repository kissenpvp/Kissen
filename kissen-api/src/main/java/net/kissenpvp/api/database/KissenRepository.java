package net.kissenpvp.api.database;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public abstract class KissenRepository<P, T extends PersistableEntity<P>> extends SQLExecutor implements Repository<P, T>
{
    public KissenRepository(@NonNull DataSource dataSource) throws NullPointerException
    {
        super(dataSource);
    }

    @Override public @NonNull CompletableFuture<Void> save(@NonNull T id) throws NullPointerException
    {
        Objects.requireNonNull(id, "The entity cannot be null.");

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

}
