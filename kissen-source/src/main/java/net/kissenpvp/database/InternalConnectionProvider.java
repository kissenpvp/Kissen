package net.kissenpvp.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.kissenpvp.api.database.ConnectionProvider;
import org.flywaydb.core.Flyway;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;

public class InternalConnectionProvider implements ConnectionProvider
{
    private Flyway flyway;

    private HikariConfig config;
    private HikariDataSource dataSource;

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

    @Override public @NonNull Optional<DataSource> dataSource()
    {
        return Optional.ofNullable(dataSource);
    }

    @Override
    public void connect(@NonNull String url, @NonNull String username, @NonNull String password) throws IllegalStateException, SQLException
    {
        connect(url, username, password, true);
    }

    @Override
    public void connect(@NonNull String url, @NonNull String username, @NonNull String password, boolean generateSchema) throws IllegalStateException, SQLException
    {
        Objects.requireNonNull(url, "Connection string must not be null");

        if (Objects.nonNull(dataSource))
        {
            throw new IllegalStateException("The connection has already been opened.");
        }

        config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        dataSource = new HikariDataSource(config);

        String location = "classpath:migrations/mariadb";
        flyway = Flyway.configure().dataSource(dataSource).locations(location).load();

        if (generateSchema)
        {
            generateSchema();
        }
    }

    public void generateSchema()
    {
        if (!isConnected())
        {
            throw new IllegalStateException("Cannot generate schema without a connection");
        }

        flyway.migrate();
    }

    @Override public void disconnect() throws IllegalStateException
    {
        if (Objects.isNull(dataSource))
        {
            throw new IllegalStateException("The connection has not been opened yet.");
        }

        dataSource.close();
    }

    private boolean isConnected()
    {
        if (Objects.nonNull(dataSource))
        {
            return !dataSource.isClosed();
        }

        return false;
    }
}
