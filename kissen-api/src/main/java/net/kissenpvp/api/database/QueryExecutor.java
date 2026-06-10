package net.kissenpvp.api.database;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Is used when database operations are done inside a {@link Repository}.
 *
 * @param <X> the return values type of the operation
 * @author Ivo Quiring
 */
@FunctionalInterface
public interface QueryExecutor<X>
{
    /**
     * Invoked after a {@link java.sql.PreparedStatement} has been created and initialized.
     *
     * <p>
     * This method is responsible for configuring and executing the provided statement.
     * Implementations should set any required parameters on the statement and execute it
     * (e.g., via {@link java.sql.PreparedStatement#executeQuery()},
     * {@link java.sql.PreparedStatement#executeUpdate()}, or {@link java.sql.PreparedStatement#execute()}),
     * depending on the intended operation.
     *
     * <p>
     * The lifecycle of the statement is managed externally; this method should focus solely
     * on preparing and executing the SQL statement and returning the resulting data if applicable.
     *
     * @param statement the initialized {@link java.sql.PreparedStatement} ready for execution
     * @return the result of the query execution, or {@code null} if the operation does not produce a result
     * @throws java.sql.SQLException if a database access error occurs during statement execution
     */
    @Nullable X executeQuery(@NonNull PreparedStatement statement) throws SQLException;
}
