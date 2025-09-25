package net.kissenpvp.network.actor.properties;

import com.google.gson.JsonObject;
import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.api.network.actor.PlayerProperties;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class PlayerPropertyMap extends HashMap<String, JsonObject> implements PlayerProperties
{
    private final PlayerClient player;
    private final PropertyRepository executor;
    private int saved;

    public PlayerPropertyMap(@NonNull PlayerClient player, @NonNull PropertyRepository executor, @NonNull Map<String, JsonObject> map)
    {
        super(map);
        this.player = player;
        this.executor = executor;

        saved = hashCode();
    }

    @Override public @NonNull PlayerClient player()
    {
        return player;
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

        return executor().put(player(), properties).thenRun(() -> saved = hashCode());
    }

    public @NonNull PropertyRepository executor()
    {
        return executor;
    }
}
