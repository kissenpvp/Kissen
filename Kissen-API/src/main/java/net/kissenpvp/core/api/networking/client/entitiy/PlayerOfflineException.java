package net.kissenpvp.core.api.networking.client.entitiy;

import org.jetbrains.annotations.NotNull;

public class PlayerOfflineException extends RuntimeException {

    private final String name;

    public PlayerOfflineException(@NotNull String name) {
        this.name = name;
    }

    public @NotNull String getName() {
        return name;
    }
}
