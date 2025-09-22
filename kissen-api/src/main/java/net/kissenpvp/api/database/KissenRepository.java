package net.kissenpvp.api.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public abstract class KissenRepository<P, T extends PersistableEntity<P>> implements Repository<P, T>
{
    private static final Logger log = LoggerFactory.getLogger(KissenRepository.class);
    private final Connection connection;

    public KissenRepository(@NotNull Connection connection) throws NullPointerException
    {
        Objects.requireNonNull(connection, "The database connection cannot be null.");

        this.connection = connection;
    }

    @Override public @NotNull CompletableFuture<Void> save(@NotNull T id) throws NullPointerException
    {
        Objects.requireNonNull(id, "The entity cannot be null.");

        return saveAll(Collections.singleton(id));
    }

    /**
     * Executes the provided SQL query using the given {@code QueryExecutor}. This method logs any SQL exceptions
     * encountered during execution and rethrows them as {@link IllegalStateException}.
     *
     * @param sql           the SQL query to be executed, cannot be null
     * @param queryExecutor the executor handling the prepared statement execution cannot be null
     * @param <X>           the type of result expected from the query execution
     * @return the result of the query execution as provided by the {@code QueryExecutor}
     * @throws IllegalStateException if an exception occurs while executing the query
     * @throws NullPointerException  if the SQL query or the {@code QueryExecutor} is null
     */
    protected <X> @Nullable X query(@NotNull String sql, @NotNull QueryExecutor<X> queryExecutor) throws IllegalStateException, NullPointerException
    {
        try
        {
            return unsafeQuery(sql, queryExecutor);
        }
        catch (SQLException sqlException)
        {
            log.error("There has been an error while executing the query {}.", sql, sqlException);
            throw new IllegalStateException(sqlException);
        }
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
    protected int computeIterableSize(@NotNull Iterable<?> iterable)
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
     * Executes the provided SQL query using the given {@code QueryExecutor}.
     *
     * @param sql           the SQL query to be executed, cannot be null
     * @param queryExecutor the executor handling the prepared statement execution cannot be null
     * @param <X>           the type of result expected from the query execution
     * @return the result of the query execution as provided by the {@code QueryExecutor}
     * @throws SQLException         if an error occurs while executing the SQL query
     * @throws NullPointerException if the SQL query or the {@code QueryExecutor} is null
     */
    protected <X> @Nullable X unsafeQuery(@NotNull String sql, @NotNull QueryExecutor<X> queryExecutor) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(sql, "The SQL string cannot be null.");

        //noinspection SqlSourceToSinkFlow
        try (PreparedStatement statement = this.connection.prepareStatement(sql))
        {
            return queryExecutor.executeQuery(statement);
        }
    }
}
