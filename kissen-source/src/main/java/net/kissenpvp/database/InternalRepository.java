package net.kissenpvp.database;

import net.kissenpvp.api.database.KissenRepository;
import net.kissenpvp.api.database.PersistableEntity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.*;
import java.util.function.Function;

/**
 * Represents an abstract repository for handling persistence of entities with a specific primary key type.
 * This class provides foundational database access methods such as finding, saving, and checking for
 * the presence of entities in the database. It handles SQL query execution with proper error handling
 * and delegates entity-specific behavior to subclasses.
 *
 * @param <P> the type of the primary key
 * @param <T> the type of the entity to be persisted, which must extend {@link PersistableEntity}
 */
public abstract class InternalRepository<P, T extends PersistableEntity<P>> extends KissenRepository<P, T>
{

    public InternalRepository(@NotNull Connection connection) throws NullPointerException
    {
        super(connection);
    }

    /**
     * Collects all results from the provided {@link PreparedStatement}'s {@link ResultSet} and maps them
     * into entities of type {@code T}.
     * <p>
     * Each row in the {@link ResultSet} is converted into an entity
     * using the {@link #toEntity(ResultSet)} method implemented in a subclass. The results are returned as
     * an unmodifiable collection.
     *
     * @param statement the {@link PreparedStatement} to execute, must not be null
     * @return an unmodifiable collection of entities of type {@code T}, never null
     * @throws SQLException if an error occurs while executing the query or accessing the {@link ResultSet}
     */
    protected @NotNull Collection<T> collectResults(@NotNull PreparedStatement statement) throws SQLException
    {
        Collection<T> collection = new HashSet<>();
        try(ResultSet resultSet = statement.executeQuery())
        {
            while(resultSet.next())
            {
                collection.add(toEntity(resultSet));
            }
        }
        return Collections.unmodifiableCollection(collection);
    }

    protected @NotNull Collection<T> collectResults(@NotNull P id, @NotNull PreparedStatement statement) throws SQLException
    {
        Collection<T> collection = new HashSet<>();
        try(ResultSet resultSet = statement.executeQuery())
        {
            while(resultSet.next())
            {
                collection.add(toEntity(id, resultSet));
            }
        }
        return Collections.unmodifiableCollection(collection);
    }

    /**
     * Checks if the given {@link PreparedStatement} has any results when executed.
     * <p>
     * This method executes the provided statement as a query and determines
     * whether the result set contains at least one row.
     *
     * @param statement the {@link PreparedStatement} to execute, must not be null
     * @return {@code true} if the query executed by the {@link PreparedStatement} contains at least one row;
     *         {@code false} otherwise
     * @throws SQLException if an error occurs during the execution of the query or while processing the result set
     */
    protected static boolean hasResult(@NotNull PreparedStatement statement) throws SQLException
    {
        try(ResultSet resultSet = statement.executeQuery())
        {
            return resultSet.next();
        }
    }

    /**
     * Safely converts a given input value of type {@code X} to a corresponding output value of type {@code Y}
     * using the provided {@link Function}.
     * <p>
     * If the input value is null, the method will return null; otherwise,
     * it applies the given function to the input value.
     *
     * @param function the function used to convert the input value to the output value; must not be null
     * @param value    the input value of type {@code X} to be converted; may be null
     * @param <X>      the type of the input value
     * @param <Y>      the type of the output value
     * @return the converted value of type {@code Y} if the input value is not null,
     *         or null if the input value is null
     * @throws NullPointerException if the provided function is null
     */
    @Contract(value = "_, null -> null; _, !null -> !null", pure = true) protected static <X, Y> @Nullable Y convertSafely(@NotNull Function<X, Y> function, @Nullable X value) throws NullPointerException
    {
        Objects.requireNonNull(function, "The function cannot be null.");

        if (Objects.isNull(value))
        {
            return null;
        }

        return function.apply(value);
    }

    /**
     * Sets a value or null at the specified indices of the given {@link PreparedStatement}.
     * If the provided {@code value} is non-null, it sets the value at both the specified index and an additional
     * offset index.
     * If the {@code value} is null, it sets SQL null for the given SQL type at both indices.
     *
     * @param statement   the {@link PreparedStatement} where the value or null will be set, must not be null
     * @param index       the index at which the first value is set must align with the {@link PreparedStatement}'s
     *                    parameters
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

    protected void overrideSignature(@NotNull T entity) throws NullPointerException
    {
        Objects.requireNonNull(entity, "The entity cannot be null.");
        if (entity instanceof InternalPersistableEntity<?> persistable)
        {
            persistable.overrideSignature();
        }
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
    public abstract @NotNull T toEntity(@NotNull P id, @NotNull ResultSet resultSet) throws SQLException, NullPointerException;

    /**
     * Converts a single row of the provided {@code ResultSet} into an entity.
     * This method is expected to be implemented by subclasses to define the mapping
     * logic from the {@code ResultSet} to the entity type {@code T}.
     *
     * @param resultSet the {@code ResultSet} containing the row to be converted into an entity, must not be null
     * @return an unmodifiable view of the entity created from the current row in the {@code ResultSet}, never null
     * @throws SQLException         if an error occurs while accessing the {@code ResultSet}
     * @throws NullPointerException if the {@code ResultSet} is null
     * @see #toEntity(Object, ResultSet)
     */
    public abstract @NotNull T toEntity(@NotNull ResultSet resultSet) throws SQLException, NullPointerException;
}
