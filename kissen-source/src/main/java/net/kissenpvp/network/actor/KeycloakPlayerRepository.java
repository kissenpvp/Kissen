package net.kissenpvp.network.actor;

import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.api.network.actor.PlayerRepository;
import net.kissenpvp.database.keycloak.KeycloakCachedInternalRepository;
import net.kissenpvp.database.keycloak.KeycloakConnectionProvider;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class KeycloakPlayerRepository extends KeycloakCachedInternalRepository<UUID, PlayerClient> implements PlayerRepository
{

    private static final String SEARCH_TEMPLATE = "%s/users?%s";
    private final KeycloakConnectionProvider connectionProvider;

    public KeycloakPlayerRepository(@NonNull KeycloakConnectionProvider connectionProvider)
    {
        this.connectionProvider = connectionProvider;
    }

    @Override
    protected @NonNull KeycloakConnectionProvider connectionProvider()
    {
        return connectionProvider;
    }

    @Override
    protected @NonNull String repositoryUrl()
    {
        return "users";
    }

    public record KeycloakUser(@NonNull String uuid, @NonNull String username) { }

    @Override
    public @NonNull CompletableFuture<@NonNull Optional<PlayerClient>> findByName(@NonNull String name)
    {
        return findByName(name, true);
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Optional<UUID>> findLinkId(@NonNull UUID uuid)
    {
        return CompletableFuture.supplyAsync(() -> findSingle("exact=true", "q=id:" + uuid).map(PlayerClient::linkId));
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Optional<PlayerClient>> findByName(@NonNull String name, boolean utilizeCache)
    {
        for(PlayerClient playerClient : cachedEntries().values())
        {
            if(!Objects.equals(name, playerClient.username())) { continue; }
            return CompletableFuture.completedFuture(Optional.of(playerClient));
        }

        return CompletableFuture.supplyAsync(() -> findSingle("exact=true", "username=" + name));
    }

    @Override
    public @NonNull CompletableFuture<Collection<PlayerClient>> findAllByName(@NonNull Iterable<String> name)
    {
        return null;
    }

    @Override
    public @NonNull CompletableFuture<Collection<PlayerClient>> findAllByName(@NonNull Iterable<String> name, boolean utilizeCache)
    {
        return null;
    }

    @Override
    public boolean cached(@NonNull String name)
    {
        for(PlayerClient playerClient : cachedEntries().values())
        {
            if(!Objects.equals(name, playerClient.username())) { continue; }
            return true;
        }
        return false;
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Optional<PlayerClient>> findUncached(@NonNull UUID id)
    {
        return CompletableFuture.supplyAsync(() -> findSingle("exact=true", "q=id:" + id));
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Collection<PlayerClient>> findAllUncached(@NonNull Iterable<UUID> id)
    {
        return null;
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Collection<PlayerClient>> findAll()
    {
        return CompletableFuture.supplyAsync(() -> Set.of(find()));
    }

    @Override
    public @NonNull CompletableFuture<Void> saveAll(@NonNull Iterable<PlayerClient> id)
    {
        return null;
    }
}
