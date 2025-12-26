package net.kissenpvp.database.keycloak;

import com.google.gson.Gson;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KeycloakConnectionProvider
{
    private static final String REALM_TEMPLATE = "https://%s/realms/%s";
    private final Gson gson;
    private final HttpClient client;

    private static final Logger log = LoggerFactory.getLogger(KeycloakConnectionProvider.class);
    private final String clientId, secret, domain, realm;
    private final KeycloakTokenProvider tokenProvider;

    public KeycloakConnectionProvider(@NonNull String clientId, @NonNull String secret, @NonNull String domain, @NonNull String realm)
    {
        this.clientId = clientId;
        this.secret = secret;
        this.domain = domain;
        this.realm = realm;

        gson = new Gson();
        this.client = HttpClient.newHttpClient();
        tokenProvider = new KeycloakTokenProvider();
    }

    public @NonNull String realmUrl()
    {
        return String.format(REALM_TEMPLATE, domain, realm);
    }

    public @NonNull String token()
    {
        if(!tokenProvider.validToken())
        {
            try
            {
                tokenProvider.retrieveToken(clientId, secret, this);
            }
            catch (IOException | InterruptedException e)
            {
                log.error("The system was unable to obtain a valid token for the keycloak instance.");
                throw new RuntimeException(); // TODO add a real exception
            }
        }
        return tokenProvider.accessToken();
    }

    public @NonNull Gson gson()
    {
        return gson;
    }

    public <T> @NonNull HttpResponse<T> send(@NonNull HttpRequest request, HttpResponse. @NonNull BodyHandler<T> bodyHandler) throws IOException, InterruptedException
    {
        return client.send(request, bodyHandler);
    }

    public @NonNull KeycloakTokenProvider tokenProvider()
    {
        return tokenProvider;
    }
}
