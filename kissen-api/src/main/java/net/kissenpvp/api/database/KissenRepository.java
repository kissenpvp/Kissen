package net.kissenpvp.api.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public abstract class KissenRepository<P, T extends PersistableEntity<P>> implements Repository<P, T>
{
    private static final Logger log = LoggerFactory.getLogger(KissenRepository.class);
    private final Connection connection;
    private final String table;

    public KissenRepository(@NotNull String table, @NotNull Connection connection) throws NullPointerException
    {
        Objects.requireNonNull(table, "The table name cannot be null.");
        Objects.requireNonNull(connection, "The database connection cannot be null.");

        if (table.isBlank() || table.length() > 64 || !table.matches("^[a-zA-Z_][a-zA-Z0-9_$]{0,63}$"))
        {
            String message = "The chosen table name %s, is not valid for a database table.";
            throw new IllegalArgumentException(String.format(message, table));
        }

        this.table = table;
        this.connection = connection;
    }

    @Override public @NotNull CompletableFuture<Void> save(@NotNull T id) throws NullPointerException
    {
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

    @Override public @NotNull String table()
    {
        return table;
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
