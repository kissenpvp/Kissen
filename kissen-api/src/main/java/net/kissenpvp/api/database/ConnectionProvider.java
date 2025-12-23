package net.kissenpvp.api.database;

import org.flywaydb.core.Flyway;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Provides an interface for establishing and managing database connections.
 * <p>
 * It is responsible for connecting to, disconnecting from,
 * and managing the state of the database connection.
 *
 * @author Ivo Quiring
 */
public interface ConnectionProvider
{
    /**
     * Retrieves an optional containing the current database connection if it exists.
     * If no connection is currently established, an empty optional is returned.
     *
     * @return a {@link Optional} containing the active {@link Connection} if available, or an empty optional if no
     * connection is present
     */
    @NonNull Optional<DataSource> dataSource();


    void connect(
            @NonNull String url,
            @NonNull String username,
            @NonNull String password
    ) throws IllegalStateException;

    void setupFlyway(Flyway flyway) throws IllegalStateException;

    void setupFlyway(Flyway flyway, boolean generateSchema) throws IllegalStateException;

    /**
     * Closes the currently established database connection.
     * This method ensures any open resources or connections are properly released.
     *
     * @throws IllegalStateException if no active connection exists
     */
    void disconnect() throws IllegalStateException;
}
