package net.kissenpvp.database;

import com.google.common.base.Preconditions;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Manages a registry of pooled {@link DataSource} instances associated with instanceIds from {@link net.kissenpvp.api.database.ConnectionProvider}.
 * <p>
 * This class is responsible for creating, storing, sharing, and closing database connection pools
 * based on {@link RegistryKey} definitions. Multiple instances may share the same underlying
 * {@link DataSource}, and reference tracking is used to make sure that a pool is only closed when
 * no instances remain subscribed to it.
 * <p>
 * The lifecycle is managed as follows:
 * <ul>
 *   <li>A {@link DataSource} is created lazily when first requested via subscription.</li>
 *   <li>Multiple instance IDs may reference the same {@link RegistryKey}.</li>
 *   <li>The underlying connection pool is closed automatically when the last subscriber is removed.</li>
 * </ul>
 * <p>
 * This class does not guarantee thread safety unless externally synchronized.
 */
public class ConnectionRegistry
{
    private final Map<RegistryKey, HikariDataSource> registry;
    private final Map<UUID, RegistryKey> subscriptions ;

    public ConnectionRegistry()
    {
        this.registry = new HashMap<>();
        this.subscriptions = new HashMap<>();
    }

    /**
     * Checks whether the instance is associated with a connection.
     *
     * @param instanceId id of the provider which should be checked
     * @return whether the instanceId has any subscription, or not
     */
    public boolean isSubscribed(UUID instanceId)
    {
        Preconditions.checkNotNull(instanceId, "InstanceId must not be null");
        return subscriptions.containsKey(instanceId);
    }

    /**
     * Retrieves the {@link DataSource} associated with the specified instance Id.
     * <p>
     * This method looks up the subscription registered for the given instance Id
     * and returns the corresponding {@link DataSource}. If the subscription exists
     * but no matching entry can be found in the registry, an {@link IllegalStateException}
     * is thrown, indicating an inconsistent internal state.
     *
     * @param instanceId the unique identifier of the instance whose subscription should be retrieved; must not be null
     * @return the {@link DataSource} associated with the specified instance Id
     * @throws IllegalStateException if a subscription exists for the given instance Id
     *                               but no corresponding {@link DataSource} is present
     *                               in the registry
     */
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

    /**
     * Subscribes the given instance to a {@link DataSource} identified by the provided connection details.
     * <p>
     * If no {@link DataSource} exists for the given connection information, a new one is created first.
     * The instance is then associated with the resolved or newly created registry entry.
     *
     * @param instance the unique identifier of the instance to subscribe; must not be null
     * @param url the database connection URL; must not be null
     * @param user the database username; must not be null
     * @param password the database password; must not be null
     */
    public void subscribe(@NonNull UUID instance, @NonNull String url, @NonNull String user, String password)
    {
        Preconditions.checkNotNull(instance, "InstanceId must not be null");

        RegistryKey registryKey = new RegistryKey(url, user);
        if(!registry.containsKey(registryKey))
        {
            create(registryKey, password);
        }

        subscribe(instance, registryKey);
    }

    /**
     * Unsubscribes the specified instance from its associated {@link DataSource}.
     * <p>
     * The instance is removed from the subscription registry. If no other instance
     * is associated with the same {@link RegistryKey}, the underlying {@link DataSource}
     * is closed.
     *
     * @param instanceId the unique identifier of the instance to unsubscribe, must not be null
     * @throws NullPointerException if {@code instanceId} is null
     * @throws IllegalStateException if the instance is not currently subscribed
     */
    public void unsubscribe(@NonNull UUID instanceId)
    {
        Preconditions.checkNotNull(instanceId, "InstanceId must not be null.");

        if(!subscriptions.containsKey(instanceId))
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

    /**
     * Registers an association between the given instance and an existing {@link RegistryKey}.
     * <p>
     * The provided instance must not already be subscribed. The specified {@link RegistryKey}
     * must already exist in the registry prior to subscription.
     *
     * @param instanceId the unique identifier of the instance to subscribe, must not be null
     * @param registryKey the registry key representing an existing {@link DataSource}, must not be null
     * @throws IllegalStateException if the instance is already subscribed or if the registry does not contain the specified key
     */
    private void subscribe(@NonNull UUID instanceId, @NonNull RegistryKey registryKey)
    {
        Preconditions.checkNotNull(instanceId, "InstanceId must not be null");
        Preconditions.checkNotNull(registryKey, "RegistryKey must not be null");

        if(subscriptions.containsKey(instanceId))
        {
            throw new IllegalStateException("This instanceId is already subscribed; unsubscribe first.");
        }

        if(!registry.containsKey(registryKey))
        {
            throw new IllegalStateException("Such a registry key is not registered.");
        }

        subscriptions.put(instanceId, registryKey);
    }

    /**
     * Creates and registers a new {@link DataSource} for the given {@link RegistryKey}.
     * <p>
     * A new {@link HikariDataSource} is created using the connection details provided
     * by the registry key and stored in the internal registry. If a data source already
     * exists for the given key, an exception is thrown and no changes are made.
     *
     * @param registryKey the registry key containing connection configuration; must not be null
     * @param password the password of the database
     * @throws IllegalStateException if a {@link DataSource} already exists for the given key
     */
    private void create(@NonNull RegistryKey registryKey, String password)
    {
        if(registry.containsKey(registryKey))
        {
            String message = "A datasource with the registry key already exists";
            throw new IllegalStateException(String.format(message, registryKey));
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(registryKey.url());
        config.setUsername(registryKey.user());
        config.setPassword(password);
        config.setMaximumPoolSize(10);

        HikariDataSource hikariSource = new HikariDataSource(config);
        registry.put(registryKey, hikariSource);
    }

    /**
     * Closes and removes the {@link DataSource} associated with the given {@link RegistryKey}.
     * <p>
     * The corresponding entry is removed from the internal registry. If no data source
     * is associated with the provided key, an exception is thrown.
     *
     * @param registryKey the registry key whose associated data source should be closed, must not be null
     * @throws NullPointerException if {@code registryKey} is null
     * @throws IllegalStateException if no {@link DataSource} is registered for the given key
     */
    private void close(@NonNull RegistryKey registryKey)
    {
        Preconditions.checkNotNull(registryKey, "RegistryKey must not be null.");

        HikariDataSource hikariDataSource = registry.remove(registryKey);
        if(Objects.isNull(hikariDataSource))
        {
            throw new IllegalStateException("RegistryKey is not registered and has therefore no connection");
        }
    }

    /**
     * Immutable value object representing a database connection configuration.
     * <p>
     * A {@code RegistryKey} uniquely identifies a {@link DataSource} based on its
     * connection URL and username. It is used as the key in the internal
     * registry for managing pooled database connections.
     * <p>
     * Instances are validated to make sure none of the fields are {@code null}.
     *
     * @param url the JDBC connection URL; must not be null
     * @param user the database username; must not be null
     */
    private record RegistryKey(@NonNull String url, @NonNull String user)
    {
        public RegistryKey
        {
            Preconditions.checkNotNull(url, "Url must not be null.");
            Preconditions.checkNotNull(user, "User must not be null.");
        }
    }
}
