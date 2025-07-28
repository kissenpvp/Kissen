package net.kissenpvp.database;

import net.kissenpvp.api.database.ConnectionProvider;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;

public class InternalConnectionProvider implements ConnectionProvider
{
    private Connection connection;

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

    @Override public void connect(@NotNull String connectionString) throws IllegalStateException, SQLException
    {
        Objects.requireNonNull(connectionString, "Connection string must not be null");

        if(Objects.nonNull(connection))
        {
            throw new IllegalStateException("The connection has already been opened.");
        }

        connection = DriverManager.getConnection(connectionString);
    }

    @Override public void disconnect() throws IllegalStateException
    {
        if(Objects.isNull(connection))
        {
            throw new IllegalStateException("The connection has not been opened yet.");
        }
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
