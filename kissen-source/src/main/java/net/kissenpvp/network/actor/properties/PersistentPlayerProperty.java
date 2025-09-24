package net.kissenpvp.network.actor.properties;

import com.google.gson.JsonObject;
import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.api.network.actor.properties.PlayerProperty;
import org.jspecify.annotations.NonNull;

public record PersistentPlayerProperty(@NonNull PlayerClient player, @NonNull String key, @NonNull JsonObject value) implements PlayerProperty { }
