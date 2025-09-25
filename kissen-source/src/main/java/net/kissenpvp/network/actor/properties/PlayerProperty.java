package net.kissenpvp.network.actor.properties;

import com.google.gson.JsonObject;
import org.jspecify.annotations.NonNull;

public record PlayerProperty(@NonNull String key, @NonNull JsonObject value) { }
