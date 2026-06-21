package net.kissenpvp.database;

import com.google.common.base.Preconditions;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class ConnectionRegistry
{
    private Map<RegistryKey, HikariDataSource> registry;
    private Map<UUID, RegistryKey> subscriptions ;

    public boolean isSubscribed(UUID instanceId)
    {
        Preconditions.checkNotNull(instanceId, "InstanceId must not be null");
        return subscriptions.containsKey(instanceId);
    }

    public @NonNull DataSource getSubscription(@NonNull UUID instanceId)
    {
        Preconditions.checkNotNull(instanceId, "InstanceId must not be null");
        RegistryKey registryKey = subscriptions.get(instanceId);

        if(!registry.containsKey(registryKey))
        {
            throw new IllegalStateException("An critical error has occurred. A subscription without any real connection has been detected.");
        }

        return registry.get(registryKey);
    }

    public @NonNull DataSource subscribe(@NonNull UUID instance, @NonNull String url, @NonNull String user, @NonNull String password)
    {
        Preconditions.checkNotNull(instance, "InstanceId must not be null");

        RegistryKey registryKey = new RegistryKey(url, user, password);
        return subscribe(instance, registryKey).orElseGet(() -> {
            DataSource dataSource = create(registryKey);
            subscriptions.put(instance, registryKey);
            return dataSource;
        });
    }

    public void unsubscribe(@NonNull UUID instanceId)
    {
        Preconditions.checkNotNull(instanceId, "InstanceId must not be null.");

        if(subscriptions.containsKey(instanceId))
        {
            throw new IllegalStateException("Instance is not subscribed to anything");
        }

        RegistryKey registryKey = subscriptions.get(instanceId);
        subscriptions.remove(instanceId);

        if(subscriptions.containsValue(registryKey))
        {
            return;
        }

        close(registryKey);
    }

    private @NonNull Optional<DataSource> subscribe(@NonNull UUID instanceId, @NonNull RegistryKey registryKey)
    {
        Preconditions.checkNotNull(instanceId, "InstanceId must not be null");
        Preconditions.checkNotNull(registryKey, "RegistryKey must not be null");

        if(!registry.containsKey(registryKey))
        {
            return Optional.empty();
        }
        return Optional.of(registry.get(registryKey));
    }

    private @NonNull DataSource create(@NonNull RegistryKey registryKey)
    {
        if(registry.containsKey(registryKey))
        {
            String message = "A datasource with the registry key already exists";
            throw new IllegalStateException(String.format(message, registryKey));
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(registryKey.url());
        config.setUsername(registryKey.user());
        config.setPassword(registryKey.password());
        config.setMaximumPoolSize(10);

        HikariDataSource hikariSource = new HikariDataSource(config);
        registry.put(registryKey, hikariSource);
        return hikariSource;
    }

    private void close(@NonNull RegistryKey registryKey)
    {
        Preconditions.checkNotNull(registryKey, "RegistryKey must not be null.");

        HikariDataSource hikariDataSource = registry.remove(registryKey);
        if(Objects.isNull(hikariDataSource))
        {
            throw new IllegalStateException("RegistryKey is not registered and has therefore no connection");
        }
    }

    private record RegistryKey(@NonNull String url, @NonNull String user, @NonNull String password)
    {
        public RegistryKey
        {
            Preconditions.checkNotNull(url, "Url must not be null.");
            Preconditions.checkNotNull(user, "User must not be null.");
            Preconditions.checkNotNull(password, "Password must not be null.");
        }

        @Override public @NonNull String toString()
        {
            // we don't want the password to be part of the string representation
            return "ConnectionRegistryKey{" +
                    "url='" + url + '\'' +
                    ", user='" + user + '\'' +
                    '}';
        }
    }
}
