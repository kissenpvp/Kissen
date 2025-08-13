package net.kissenpvp.api.base;


import net.kissenpvp.api.localization.GlobalLocaleRegistry;
import net.kissenpvp.api.network.actor.PlayerModule;
import net.kissenpvp.api.network.actor.rank.RankModule;
import net.kissenpvp.api.punishment.PunishmentModule;
import org.jetbrains.annotations.NotNull;

public interface Kissen
{
    @NotNull PunishmentModule punishmentModule();

    @NotNull RankModule rankModule();

    @NotNull PlayerModule playerModule();

    @NotNull GlobalLocaleRegistry localeRegistry();

    boolean started();
}
