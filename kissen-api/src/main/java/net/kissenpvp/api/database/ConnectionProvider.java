package net.kissenpvp.api.database;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

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
    @NotNull Optional<HikariDataSource> dataSource();


    /**
     * Establishes a connection to the database using the provided URL, username, and password.
     * Before invoking this method, ensure the URL and credentials are correctly configured.
     * <p>
     * Note that typically implementations will generate the database schema upon establishing the connection.
     * This behavior can be overridden by specifying {@code false} for the {@code generateSchema} parameter in
     * {@link #connect(String, String, String, boolean)}.
     *
     * @param url      the database connection URL, which specifies the database location, must not be null
     * @param username the username for authenticating with the database; must not be null
     * @param password the password for authenticating with the database; must not be null
     * @throws IllegalStateException if a connection is already established
     * @throws SQLException          if the connection attempt fails or a database access error occurs
     * @throws NullPointerException  if any of the parameters are null
     * @see #connect(String, String, String, boolean)
     */
    void connect(
            @NotNull String url,
            @NotNull String username,
            @NotNull String password
    ) throws IllegalStateException, SQLException, NullPointerException;


    /**
     * Establishes a connection to the database with the provided URL, username, and password.
     * This method also allows specifying whether the database schema should be generated.
     *
     * @param url            the database connection URL, which specifies the database location, must not be null
     * @param username       the username for authenticating with the database; must not be null
     * @param password       the password for authenticating with the database; must not be null
     * @param generateSchema a boolean indicating whether the database schema should be generated; {@code true} to
     *                       generate the schema, {@code false} otherwise
     * @throws IllegalStateException if a connection is already established
     * @throws SQLException          if a database access error occurs or the connection attempt fails
     * @throws NullPointerException  if any of the parameters are null
     * @see #connect(String, String, String)
     */
    void connect(
            @NotNull String url,
            @NotNull String username,
            @NotNull String password,
            boolean generateSchema
    ) throws IllegalStateException, SQLException, NullPointerException;

    /**
     * Closes the currently established database connection.
     * This method ensures any open resources or connections are properly released.
     *
     * @throws IllegalStateException if no active connection exists
     */
    void disconnect() throws IllegalStateException;
}
