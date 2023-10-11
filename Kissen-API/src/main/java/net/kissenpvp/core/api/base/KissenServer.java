package net.kissenpvp.core.api.base;

import org.jetbrains.annotations.NotNull;

public interface KissenServer
{

    @NotNull String getMotd();

    @NotNull String getServerVersion();

    int getPlayerCount();

    int getMaxPlayers();

}
