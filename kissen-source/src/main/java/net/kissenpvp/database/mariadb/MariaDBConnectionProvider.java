package net.kissenpvp.database.mariadb;

import com.google.common.base.Preconditions;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.kissenpvp.api.database.ConnectionProvider;
import org.flywaydb.core.Flyway;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;

public class MariaDBConnectionProvider implements ConnectionProvider
{
    private Flyway flyway;

    private HikariDataSource dataSource;

    public MariaDBConnectionProvider() throws MissingResourceException
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
    public void connect(@NonNull String url, @NonNull String username, @NonNull String password) throws IllegalStateException
    {
        Preconditions.checkNotNull(url, "Connection string must not be null");

        if (Objects.nonNull(dataSource))
        {
            throw new IllegalStateException("The connection has already been opened.");
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        dataSource = new HikariDataSource(config);
    }

    @Override
    public void setupFlyway(@NonNull Flyway flyway) throws IllegalStateException
    {
        setupFlyway(flyway, false);
    }

    @Override
    public void setupFlyway(@NonNull Flyway flyway, boolean generateSchema)
    {
        Preconditions.checkNotNull(flyway, "Flyway must not be null.");
        Preconditions.checkArgument(Objects.isNull(this.flyway), "Flyway has already been initialized.");

        if(!isConnected())
        {
            throw new IllegalStateException("Cannot setup flyway before being connected.");
        }

        this.flyway = flyway;
        if (!generateSchema) { return; }

        generateSchema();
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
