package net.kissenpvp.database;

import net.kissenpvp.api.database.PersistableEntity;
import net.kissenpvp.api.database.Repository;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.StreamSupport;

/**
 * Represents an abstract repository for handling persistence of entities with a specific primary key type.
 * This class provides foundational database access methods such as finding, saving, and checking for
 * the presence of entities in the database. It handles SQL query execution with proper error handling
 * and delegates entity-specific behavior to subclasses.
 *
 * @param <P> the type of the primary key
 * @param <T> the type of the entity to be persisted, which must extend {@link PersistableEntity}
 */
public abstract class InternalRepository<P, T extends PersistableEntity<P>> implements Repository<P, T>
{
    private static final Logger log = LoggerFactory.getLogger(InternalRepository.class);
    private final Connection connection;
    private final String table, findQuery;

    @Contract(value = "_, null -> null; _, !null -> !null", pure = true)
    protected static <X, Y> @Nullable Y convertSafely(@NotNull Function<X, Y> function, @Nullable X value) throws NullPointerException {
        Objects.requireNonNull(function, "The function cannot be null.");

        if (Objects.isNull(value)) {
            return null;
        }

        return function.apply(value);
    }
    
    public InternalRepository(@NotNull String table, @NotNull Connection connection, @NotNull String findQuery) throws NullPointerException
    {
        Objects.requireNonNull(table, "The table name cannot be null.");
        Objects.requireNonNull(connection, "The database connection cannot be null.");
        Objects.requireNonNull(findQuery, "The find query cannot be null.");

        if (table.isBlank() || table.length() > 64 || !table.matches("^[a-zA-Z_][a-zA-Z0-9_$]{0,63}$"))
        {
            String message = "The chosen table name %s, is not valid for a database table.";
            throw new IllegalArgumentException(String.format(message, table));
        }

        this.table = table;
        this.connection = connection;
        this.findQuery = findQuery;
    }

    /**
     * Sets a value or null at the specified indices of the given {@link PreparedStatement}.
     * If the provided {@code value} is non-null, it sets the value at both the specified index and an additional offset index.
     * If the {@code value} is null, it sets SQL null for the given SQL type at both indices.
     *
     * @param statement   the {@link PreparedStatement} where the value or null will be set, must not be null
     * @param index       the index at which the first value is set must align with the {@link PreparedStatement}'s parameters
     * @param secondIndex the index offset that serves as the base for the second insertion
     * @param sqlType     the SQL type, defined in {@link java.sql.Types}, used to set the value or null
     * @param value       the value to be set; can be null in which case SQL null will be inserted
     * @throws SQLException         if an error occurs while interacting with the {@link PreparedStatement}
     * @throws NullPointerException if the {@link PreparedStatement} is null
     */
    protected static void setDual(@NotNull PreparedStatement statement, int index, int secondIndex, int sqlType, @Nullable Object value) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(statement, "The prepared statement cannot be null.");

        if (Objects.nonNull(value))
        {
            statement.setObject(index, value, sqlType);
            statement.setObject(secondIndex, value, sqlType);
            return;
        }

        statement.setNull(index, sqlType);
        statement.setNull(secondIndex, sqlType);
    }

    @Override public @NotNull CompletableFuture<@Nullable T> find(@NotNull P id) throws NullPointerException
    {
        Objects.requireNonNull(id, "The identifier cannot be null.");

        return CompletableFuture.supplyAsync(() -> query(findQuery, (statement ->
        {
            statement.setObject(1, id);

            try (ResultSet resultSet = statement.executeQuery())
            {
                if (!resultSet.next())
                {
                    return null;
                }

                return toEntity(id, resultSet);
            }
        })));
    }

    @Override public @NotNull CompletableFuture<@UnmodifiableView Collection<T>> findAll(@NotNull Iterable<P> id) throws NullPointerException
    {
        Objects.requireNonNull(id, "The identifier cannot be null.");

        List<P> primaryKeys = StreamSupport.stream(id.spliterator(), false).toList();

        if (primaryKeys.isEmpty())
        {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        String placeholders = String.join(",", Collections.nCopies(primaryKeys.size(), "?"));
        String sql = "SELECT * FROM %s WHERE id IN (" + placeholders + ");";

        return CompletableFuture.supplyAsync(() -> query(sql, statement ->
        {
            int index = 1;
            for (P current : primaryKeys)
            {
                statement.setObject(index++, current);
            }

            try (ResultSet resultSet = statement.executeQuery())
            {
                return toEntities(resultSet);
            }
        }));
    }

    @Override public @NotNull CompletableFuture<@UnmodifiableView Collection<T>> findAll()
    {
        return CompletableFuture.supplyAsync(() -> query("SELECT * FROM %s;", (statement ->
        {
            try (ResultSet resultSet = statement.executeQuery())
            {
                return toEntities(resultSet);
            }
        })));
    }

    @Override public @NotNull CompletableFuture<Boolean> has(@NotNull P id) throws NullPointerException
    {
        Objects.requireNonNull(id, "The identifier cannot be null.");

        return CompletableFuture.supplyAsync(() -> query("SELECT id FROM %s WHERE id = ?;", (statement ->
        {
            statement.setObject(1, id);
            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next();
            }
        })));
    }

    @Override public @NotNull CompletableFuture<Void> save(@NotNull T id) throws NullPointerException
    {
        Objects.requireNonNull(id, "The entity cannot be null.");
        return saveAll(Collections.singleton(id));
    }

    protected void overrideSignature(@NotNull T entity) throws NullPointerException
    {
        Objects.requireNonNull(entity, "The entity cannot be null.");
        if (entity instanceof InternalPersistableEntity<?> persistable)
        {
            persistable.overrideSignature();
        }
    }

    public @NotNull String table()
    {
        return table;
    }

    /**
     * Converts a single row of the provided {@code ResultSet} into an entity.
     * This method is expected to be implemented by subclasses to define the mapping logic from the {@code ResultSet}
     * data and the specified {@code id} to the entity type {@code T}.
     *
     * @param id        the identifier associated with the entity being constructed must not be null
     * @param resultSet the {@code ResultSet} containing the row to be converted into an entity, must not be null
     * @return the entity created from the specified {@code id} and the current row in the {@code ResultSet}, never null
     * @throws SQLException         if an error occurs while accessing the {@code ResultSet}
     * @throws NullPointerException if either {@code id} or {@code resultSet} is null
     */
    protected abstract @NotNull T toEntity(@NotNull P id, @NotNull ResultSet resultSet) throws SQLException, NullPointerException;

    /**
     * Converts a single row of the provided {@code ResultSet} into an entity.
     * This method is expected to be implemented by subclasses to define the mapping
     * logic from the {@code ResultSet} to the entity type {@code T}.
     *
     * @param resultSet the {@code ResultSet} containing the row to be converted into an entity, must not be null
     * @return an unmodifiable view of the entity created from the current row in the {@code ResultSet}, never null
     * @throws SQLException         if an error occurs while accessing the {@code ResultSet}
     * @throws NullPointerException if the {@code ResultSet} is null
     * @see #toEntities(ResultSet)
     * @see #toEntity(Object, ResultSet)
     */
    protected abstract @NotNull T toEntity(@NotNull ResultSet resultSet) throws SQLException, NullPointerException;

    /**
     * Converts all rows of the provided {@code ResultSet} into a collection of entities.
     * Each row in the {@code ResultSet} is processed and transformed into an entity
     * using the {@link #toEntity(ResultSet)} method.
     *
     * @param resultSet the {@code ResultSet} containing rows of data to be converted into entities, must not be null
     * @return an unmodifiable collection of entities created from the rows in the {@code ResultSet}, never null
     * @throws SQLException         if an error occurs while accessing the {@code ResultSet}
     * @throws NullPointerException if the {@code ResultSet} is null
     * @see #toEntity(ResultSet)
     */
    private @NotNull @UnmodifiableView Collection<T> toEntities(@NotNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        List<T> data = new ArrayList<>();
        while (resultSet.next())
        {
            data.add(toEntity(resultSet));
        }
        return Collections.unmodifiableList(data);
    }

    /**
     * Loads all rows from the given {@code ResultSet} and converts them into a collection of entities.
     * Each row in the {@code ResultSet} is transformed into an entity using the {@link #toEntity(ResultSet)} method.
     *
     * @param resultSet the {@code ResultSet} containing data to be converted into entities, cannot be null
     * @return a collection of entities derived from the {@code ResultSet}, never null
     * @throws SQLException         if an SQL error occurs while processing the {@code ResultSet}
     * @throws NullPointerException if the {@code ResultSet} is null
     */
    private @NotNull Collection<T> loadAll(@NotNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(resultSet, "The ResultSet cannot be null.");

        List<T> data = new ArrayList<>();
        while (resultSet.next())
        {
            data.add(toEntity(resultSet));
        }
        return data;
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
    protected <X> @NotNull X query(@NotNull String sql, @NotNull QueryExecutor<X> queryExecutor) throws IllegalStateException, NullPointerException
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
     * Executes the provided SQL query using the given {@code QueryExecutor}.
     *
     * @param sql           the SQL query to be executed, cannot be null
     * @param queryExecutor the executor handling the prepared statement execution cannot be null
     * @param <X>           the type of result expected from the query execution
     * @return the result of the query execution as provided by the {@code QueryExecutor}
     * @throws SQLException         if an error occurs while executing the SQL query
     * @throws NullPointerException if the SQL query or the {@code QueryExecutor} is null
     */
    protected <X> @NotNull X unsafeQuery(@NotNull String sql, @NotNull QueryExecutor<X> queryExecutor) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(sql, "The SQL string cannot be null.");

        //noinspection SqlSourceToSinkFlow
        try (PreparedStatement statement = this.connection.prepareStatement(String.format(sql, table())))
        {
            return Objects.requireNonNull(queryExecutor.executeQuery(statement));
        }
    }
}
