package net.kissenpvp.core.api.networking.client.entitiy;

import io.netty.channel.Channel;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface OnlinePlayerClient extends MessageReceiver {

    void kick(@NotNull String message);

    void kick(@NotNull Component component);

    void killConnection();

    @NotNull Channel getConnection();

    void sendPacket(Object object) throws ClassNotFoundException;

    @Nullable String getCurrentServer();
}
