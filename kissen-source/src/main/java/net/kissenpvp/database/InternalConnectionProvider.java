package net.kissenpvp.database;

import net.kissenpvp.api.database.ConnectionProvider;
import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public class InternalConnectionProvider implements ConnectionProvider, DataSource
{
    private PrintWriter logWriter;

    private Connection connection;
    private Flyway flyway;
    private String url;

    private int loginTimeout;

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

    @Override
    public void connect(@NotNull String url, @NotNull String username, @NotNull String password) throws IllegalStateException, SQLException
    {
        connect(url, username, password, true);
    }

    @Override
    public void connect(@NotNull String url, @NotNull String username, @NotNull String password, boolean generateSchema) throws IllegalStateException, SQLException
    {
        Objects.requireNonNull(url, "Connection string must not be null");

        if(Objects.nonNull(connection))
        {
            throw new IllegalStateException("The connection has already been opened.");
        }

        this.url = url;
        connection = DriverManager.getConnection(url, username, password);

        String location = "classpath:migrations/mariadb";
        flyway = Flyway.configure().dataSource(url, username, password).locations(location).load();

        if(generateSchema)
        {
            generateSchema();
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
                return !connection.isClosed() && connection.isValid(loginTimeout);
            }
        }
        catch (SQLException ignored) {}

        return false;
    }

    @Override public @NotNull Connection getConnection() throws SQLException
    {
        return connection;
    }

    @Override public @NotNull Connection getConnection(@NotNull String username, @NotNull String password) throws SQLException
    {
        if(Objects.isNull(url))
        {
            throw new SQLException("The url has not yet been set.");
        }

        connect(url, username, password);
        return connection;
    }

    @Override public @Nullable PrintWriter getLogWriter() throws SQLException
    {
        return logWriter;
    }

    @Override public void setLogWriter(@Nullable PrintWriter out) throws SQLException
    {
        logWriter = out;
    }

    @Override public void setLoginTimeout(int seconds) throws SQLException
    {
        if (seconds < 0)
        {
            throw new SQLException("Login timeout must be greater than or equal to 0.");
        }
        loginTimeout = seconds;
        DriverManager.setLoginTimeout(seconds);
    }

    @Override public int getLoginTimeout()
    {
        return this.loginTimeout;
    }

    @Override public Logger getParentLogger() throws SQLFeatureNotSupportedException
    {
        throw new SQLFeatureNotSupportedException("java.util.logging is not supported by this DataSource.");
    }

    @Override public <T> T unwrap(@NotNull Class<T> iface) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(iface, "The interface cannot be null.");

        if (iface.isInstance(this))
        {
            return iface.cast(this);
        }
        throw new SQLException("No wrapper for " + iface.getName());
    }

    @Override public boolean isWrapperFor(@NotNull Class<?> iface) throws NullPointerException
    {
        Objects.requireNonNull(iface, "The interface cannot be null.");

        return iface.isInstance(this);
    }
}
