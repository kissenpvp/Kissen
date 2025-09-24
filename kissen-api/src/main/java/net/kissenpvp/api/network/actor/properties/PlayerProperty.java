package net.kissenpvp.api.network.actor.properties;

import com.google.gson.JsonObject;
import net.kissenpvp.api.network.actor.PlayerClient;
import org.jspecify.annotations.NonNull;

public interface PlayerProperty
{
    @NonNull PlayerClient player();

    @NonNull String key();

    @NonNull JsonObject value();
}
