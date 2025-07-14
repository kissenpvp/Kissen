package net.kissenpvp.api.base;


import net.kissenpvp.api.database.Repository;
import net.kissenpvp.api.localization.GlobalLocaleRegistry;
import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.api.network.actor.rank.RankModule;
import net.kissenpvp.api.punishment.PunishmentModule;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface Kissen
{
    @NotNull PunishmentModule punishmentModule();

    @NotNull RankModule rankModule();

    @NotNull GlobalLocaleRegistry localeRegistry();

    @NotNull Repository<UUID, PlayerClient> playerRepository();

    boolean started();
}
