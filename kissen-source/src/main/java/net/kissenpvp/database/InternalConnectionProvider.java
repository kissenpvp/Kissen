package net.kissenpvp.database;

import net.kissenpvp.api.database.ConnectionProvider;
import org.jetbrains.annotations.NotNull;

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

    public void generateSchema() throws IOException, SQLException
    {
        if(!isConnected())
        {
            throw new IllegalStateException("Cannot generate schema without a connection");
        }
        for(String sql : loadSchema())
        {
            connection.prepareStatement(sql).execute();
        }
    }

    private @NotNull String[] loadSchema() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream resourceAsStream = classLoader.getResourceAsStream("schema.sql");

        if(Objects.isNull(resourceAsStream))
        {
            throw new IllegalStateException("There has been an issue when loading the schema.sql resource. It could not be found.");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream))) {
            return String.join("", reader.lines().toList()).split(";");
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
