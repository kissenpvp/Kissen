package net.kissenpvp.core.api.networking.client.entitiy;

import io.netty.channel.Channel;
import net.kissenpvp.core.api.ban.Punishment;
import net.kissenpvp.core.api.permission.Permission;
import net.kissenpvp.core.api.user.rank.PlayerRank;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface OnlinePlayerClient<P extends Permission, R extends PlayerRank<?>, B extends Punishment<?>> extends PlayerClient<P, R, B> {

    void kick(@NotNull String message);

    void kick(@NotNull Component component);

    void killConnection();

    @NotNull Channel getConnection();

    void sendPacket(@NotNull Object object) throws ClassNotFoundException, ClassCastException;

    @Nullable String getCurrentServer();

    @NotNull Audience getKyoriAudience();
}
