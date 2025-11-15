package net.kissenpvp.api.database;

import com.google.common.base.Preconditions;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLExecutor
{

    private static final Logger log = LoggerFactory.getLogger(SQLExecutor.class);
    private final DataSource dataSource;


    public SQLExecutor(@NonNull DataSource dataSource) throws NullPointerException
    {
        Preconditions.checkNotNull(dataSource, "The dataSource cannot be null.");
        this.dataSource = dataSource;
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
    protected <X> @Nullable X query(@NonNull String sql, @NonNull QueryExecutor<X> queryExecutor) throws IllegalStateException, NullPointerException
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
    protected <X> @Nullable X unsafeQuery(@NonNull String sql, @NonNull QueryExecutor<X> queryExecutor) throws SQLException, NullPointerException
    {
        Preconditions.checkNotNull(sql, "The SQL string cannot be null.");

        try (Connection connection = dataSource().getConnection()) {
            //noinspection SqlSourceToSinkFlow
            try (PreparedStatement statement = connection.prepareStatement(sql))
            {
                return queryExecutor.executeQuery(statement);
            }
        }
    }

    protected DataSource dataSource()
    {
        return dataSource;
    }
}
