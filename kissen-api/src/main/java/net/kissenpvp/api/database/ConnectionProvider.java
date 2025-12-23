package net.kissenpvp.api.database;

import org.flywaydb.core.Flyway;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.sql.Connection;
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

    /**
     * Establishes a connection to a database using HikariCP.
     * <p>
     * This method initializes and opens a new database connection pool.
     * It must not be called while an existing connection is active; attempting
     * to do so will result in an {@link IllegalStateException}.
     *
     * @param url the JDBC URL of the database to connect to
     * @param username the username used for database authentication
     * @param password the password used for database authentication
     * @throws IllegalStateException if a connection has already been established
     */
    void connect(
            @NonNull String url,
            @NonNull String username,
            @NonNull String password
    ) throws IllegalStateException;

    /**
     * Configures Flyway for database migrations.
     * <p>
     * This method applies the required Flyway settings
     * It must not be called after Flyway has already been configured or initialized.
     *
     * @param flyway the Flyway instance to configure
     * @throws IllegalStateException if flyway has already been set up, or the database connection is not established
     */
    void setupFlyway(@NonNull Flyway flyway) throws IllegalStateException;

    /**
     * Configures Flyway for database migrations.
     * <p>
     * This method applies the required Flyway settings and optionally applies the migrations.
     * It must not be called after Flyway has already been configured or initialized.
     *
     * @param flyway the Flyway instance to configure
     * @param generateSchema whether the database schema should be generated if it does not exist
     * @throws IllegalStateException if flyway has already been set up, or the database connection is not established
     */
    void setupFlyway(@NonNull Flyway flyway, boolean generateSchema) throws IllegalStateException;

    /**
     * Closes the currently established database connection.
     * This method ensures any open resources or connections are properly released.
     *
     * @throws IllegalStateException if no active connection exists
     */
    void disconnect() throws IllegalStateException;
}
