package net.kissenpvp.network.actor.properties;

import com.google.gson.JsonObject;
import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.api.network.actor.PlayerProperties;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class PlayerPropertyMap extends HashMap<String, JsonObject> implements PlayerProperties
{
    private final PropertyRepository repository;
    private int saved;

    public PlayerPropertyMap(@NonNull PropertyRepository repository, @NonNull Map<String, JsonObject> map)
    {
        super(map);
        this.repository = repository;

        saved = hashCode();
    }

    @Override public @NonNull PlayerClient player()
    {
        return repository.player();
    }

    @Override public boolean unsaved()
    {
        return saved != hashCode();
    }

    @Override public @NonNull CompletableFuture<Void> save()
    {
        Collection<PlayerProperty> properties = new HashSet<>(size());
        for(Map.Entry<String, JsonObject> entry : entrySet())
        {
            PlayerProperty property = new PlayerProperty(entry.getKey(), entry.getValue());
            properties.add(property);
        }

        return repository().put(properties).thenRun(() -> saved = hashCode());
    }

    public @NonNull PropertyRepository repository()
    {
        return repository;
    }
}
