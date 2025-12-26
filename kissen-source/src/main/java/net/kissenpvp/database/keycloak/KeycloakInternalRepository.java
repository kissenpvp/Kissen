package net.kissenpvp.database.keycloak;

import net.kissenpvp.api.database.PersistableEntity;
import net.kissenpvp.api.database.Repository;
import net.kissenpvp.api.network.actor.PlayerClient;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class KeycloakInternalRepository<P, T extends PersistableEntity<P>> implements Repository<P, T>
{

    protected abstract @NonNull T[] toEntity(@NonNull String response);

    protected @NonNull T @NonNull [] find(@NonNull String... query)
    {
        try
        {
            String token = connectionProvider().token();

            String url = String.format("%s/%s?%s", connectionProvider().realmUrl(), repositoryUrl(), String.join("&", query));
            URI uri = URI.create(url);

            HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Authorization", "Bearer " + token).GET().build();
            HttpResponse<String> result = connectionProvider().send(request, HttpResponse.BodyHandlers.ofString());

            if(result.statusCode() != 200)
            {
                throw new RequestFailedException(result.statusCode());
            }

            return toEntity(result.body());
        }
        catch (IOException | InterruptedException exception)
        {
            throw new RuntimeException(exception); // TODO throw real exception
        }
    }

    protected @NonNull Optional<T> findSingle(@NonNull String @NonNull ... query)
    {
        String[] queryParts = new String[query.length + 1];
        System.arraycopy(query, 0, queryParts, 1, queryParts.length - 1);
        queryParts[0] = "max=1";

        T[] results = find(queryParts);
        if(results.length == 0)
        {
            return Optional.empty();
        }

        return Optional.of(results[0]);
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Boolean> has(@NonNull P id)
    {
        return find(id).thenApply(Optional::isPresent);
    }

    @Override
    public @NonNull CompletableFuture<Void> save(@NonNull T id)
    {
        return saveAll(Collections.singleton(id));
    }

    protected abstract @NonNull KeycloakConnectionProvider connectionProvider();

    protected abstract @NonNull String repositoryUrl();
}
