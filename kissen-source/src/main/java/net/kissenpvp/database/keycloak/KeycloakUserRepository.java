package net.kissenpvp.database.keycloak;

import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.api.network.actor.PlayerRepository;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class KeycloakUserRepository implements PlayerRepository
{
    private static final String SEARCH_TEMPLATE = "%s/users?%s";
    private KeycloakConnectionProvider connectionProvider;

    public KeycloakUserRepository(@NonNull KeycloakConnectionProvider connectionProvider)
    {
        this.connectionProvider = connectionProvider;
    }

    public record KeycloakUser(@NonNull String uuid, @NonNull String username) { }

    public abstract @NonNull PlayerClient toEntity(@NonNull KeycloakUser user);

    private @NonNull PlayerClient @NonNull [] find(@NonNull String query) throws IOException, InterruptedException
    {
        String token = connectionProvider.token();
        String url = String.format(SEARCH_TEMPLATE, connectionProvider.realmUrl(), query);
        URI uri = URI.create(url);

        HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Authorization", "Bearer " + token).GET().build();
        HttpResponse<String> result = connectionProvider.send(request, HttpResponse.BodyHandlers.ofString());
        KeycloakUser[] users = connectionProvider.gson().fromJson(result.body(), KeycloakUser[].class);

        PlayerClient[] playerClients = new PlayerClient[users.length];
        for (int i = 0; i < playerClients.length; i++)
        {
            playerClients[i] = toEntity(users[i]);
        }
        return playerClients;
    }


    @Override
    public @NonNull CompletableFuture<@NonNull Optional<PlayerClient>> findByName(@NonNull String name)
    {
        return CompletableFuture.supplyAsync(() ->
        {
            try
            {
                PlayerClient[] playerClients = find("max=1&search=username:" + name);
                if (playerClients.length == 0)
                {
                    return Optional.empty();
                }

                return Optional.of(playerClients[0]);
            }
            catch (IOException | InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Optional<UUID>> findLinkId(@NonNull UUID uuid)
    {
        return CompletableFuture.supplyAsync(() ->
        {
            try
            {
                PlayerClient[] playerClients = find("max=1&search=link_id:" + uuid);
                if (playerClients.length == 0)
                {
                    return Optional.empty();
                }

                return Optional.of(playerClients[0].id());
            }
            catch (IOException | InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Optional<PlayerClient>> findByName(@NonNull String name, boolean utilizeCache)
    {
        return null;
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
        return false;
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Optional<PlayerClient>> find(@NonNull UUID id, boolean utilizeCache)
    {
        return null;
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Collection<PlayerClient>> findAll(@NonNull Iterable<UUID> id, boolean utilizeCache)
    {
        return null;
    }

    @Override
    public boolean cached(@NonNull UUID id)
    {
        return false;
    }

    @Override
    public boolean cachedAll(@NonNull Iterable<UUID> id)
    {
        return false;
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Optional<PlayerClient>> find(@NonNull UUID id)
    {
        return null;
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Collection<PlayerClient>> findAll(@NonNull Iterable<UUID> id)
    {
        return null;
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Collection<PlayerClient>> findAll()
    {
        return null;
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Boolean> has(@NonNull UUID id)
    {
        return null;
    }

    @Override
    public @NonNull CompletableFuture<Void> save(@NonNull PlayerClient id)
    {
        return null;
    }

    @Override
    public @NonNull CompletableFuture<Void> saveAll(@NonNull Iterable<PlayerClient> id)
    {
        return null;
    }
}
