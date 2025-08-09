package net.kissenpvp.database;

import net.kissenpvp.api.database.ConnectionProvider;
import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.io.*;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class InternalConnectionProvider implements ConnectionProvider
{
    private Connection connection;
    private Flyway flyway;

    public InternalConnectionProvider() throws MissingResourceException
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException classNotFoundException)
        {
            throw new MissingResourceException("The JDBC MySQL is missing.", "Driver", "com.mysql.cj.jdbc");
        }
    }

    @Override public @NotNull Optional<Connection> connection()
    {
        return Optional.ofNullable(connection);
    }

    @Override public void connect(@NotNull String connectionString) throws IllegalStateException, SQLException, IOException
    {
        connect(connectionString, true);
    }

    public void connect(@NotNull String connectionString, boolean executeSchema) throws IllegalStateException, SQLException, IOException
    {
        Objects.requireNonNull(connectionString, "Connection string must not be null");

        if(Objects.nonNull(connection))
        {
            throw new IllegalStateException("The connection has already been opened.");
        }

        connection = DriverManager.getConnection(connectionString);
        // User and Password are embedded into the connectionstring
        flyway = Flyway.configure().dataSource(connectionString, null, null).load();

        if(executeSchema)
        {
            generateSchema();
        }
    }

    @Override public void disconnect() throws IllegalStateException
    {
        if(Objects.isNull(connection))
        {
            throw new IllegalStateException("The connection has not been opened yet.");
        }
    }

    public void generateSchema()
    {
        if(!isConnected())
        {
            throw new IllegalStateException("Cannot generate schema without a connection");
        }

        flyway.migrate();
    }

    private boolean isConnected()
    {
        try
        {
            if (Objects.nonNull(connection))
            {
                return !connection.isClosed() && connection.isValid(10);
            }
        }
        catch (SQLException ignored) {}

        return false;
    }
}
