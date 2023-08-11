package net.kissenpvp.core.networking.client.entity;

import net.kissenpvp.core.api.networking.client.entitiy.OnlinePlayerClient;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;


public interface KissenOnlinePlayerClient extends OnlinePlayerClient, KissenMessageReceiver {

    @Override
    default void killConnection() {
        getConnection().close();
    }

    @Override
    default void kick(@NotNull String message) {
        kick(Component.text(message));
    }
}
