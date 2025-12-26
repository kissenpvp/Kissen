package net.kissenpvp.database.keycloak;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import net.kissenpvp.api.database.ConnectionProvider;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Objects;

public class KeycloakTokenProvider
{
    private static final Gson GSON = new Gson();
    private static final String URL_TEMPLATE = "%s/protocol/openid-connect/token";
    private static final Logger log = LoggerFactory.getLogger(KeycloakTokenProvider.class);

    private String accessToken;
    private Instant expiresAt;

    public synchronized void retrieveToken(@NonNull String clientId, @NonNull String clientSecret, @NonNull KeycloakConnectionProvider provider) throws IOException, InterruptedException, RequestFailedException
    {
        if(validToken())
        {
            log.warn("The system tried to fetch a new token while the old was still active.");
            return;
        }

        String body = "grant_type=client_credentials&client_id=%s&client_secret=%";
        URI tokenUri = URI.create(String.format(URL_TEMPLATE, provider.realmUrl()));
        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofString(String.format(body, clientId, clientSecret), StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder().uri(tokenUri).header("Content-Type", "application/x-www-form-urlencoded").POST(publisher).build();

        HttpResponse<String> response = provider.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() != 200)
        {
            log.error("While requesting a new token the server responded with {}.", response.statusCode());
            throw new RequestFailedException(response.statusCode());
        }

        TokenResponse token = GSON.fromJson(response.body(), TokenResponse.class);
        accessToken = token.accessToken();
        expiresAt = Instant.now().plusSeconds(token.expiresIn() - 10);
    }

    public boolean validToken()
    {
        return Objects.nonNull(accessToken) && Instant.now().isBefore(expiresAt);
    }

    public void discardAccessToken()
    {
        if(!validToken()) { return; }

        accessToken = null;
        log.info("The access token has been discarded.");
    }

    public @NonNull String accessToken()
    {
        Preconditions.checkNotNull(accessToken);
        return accessToken;
    }

    private record TokenResponse(@SerializedName("access_token") @NonNull String accessToken, @SerializedName("expires_in") int expiresIn, @SerializedName("token_type") @NonNull String tokenType) {}
}
