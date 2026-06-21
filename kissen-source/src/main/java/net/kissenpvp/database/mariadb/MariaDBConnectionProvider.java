package net.kissenpvp.database.mariadb;

import com.google.common.base.Preconditions;
import net.kissenpvp.api.database.ConnectionProvider;
import net.kissenpvp.base.KissenCore;
import net.kissenpvp.database.ConnectionRegistry;
import org.flywaydb.core.Flyway;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class MariaDBConnectionProvider implements ConnectionProvider
{
    private Flyway flyway;
    private final UUID subscriptionId;

    private static @NonNull ConnectionRegistry registry()
    {
        return KissenCore.getInstance().getConnectionRegistry();
    }

    public MariaDBConnectionProvider() throws MissingResourceException
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            subscriptionId = UUID.randomUUID();
        }
        catch (ClassNotFoundException classNotFoundException)
        {
            throw new MissingResourceException("The JDBC MySQL is missing.", "Driver", "com.mysql.cj.jdbc");
        }
    }

    @Override public @NonNull Optional<DataSource> dataSource()
    {
        if(registry().isSubscribed(getSubscriptionId()))
        {
            return Optional.of(registry().getSubscription(getSubscriptionId()));
        }

        return Optional.empty();
    }

    @Override
    public void connect(@NonNull String url, @NonNull String username, @NonNull String password) throws IllegalStateException
    {
        Preconditions.checkNotNull(url, "Connection string must not be null");

        if (registry().isSubscribed(getSubscriptionId()))
        {
            throw new IllegalStateException("The connection has already been opened.");
        }

        registry().subscribe(getSubscriptionId(), url, username, password);
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
        if (!registry().isSubscribed(getSubscriptionId()))
        {
            throw new IllegalStateException("The connection has not been opened yet.");
        }

        registry().unsubscribe(getSubscriptionId());
    }

    private boolean isConnected()
    {
        return registry().isSubscribed(getSubscriptionId());
    }

    private @NonNull UUID getSubscriptionId()
    {
        return subscriptionId;
    }
}
