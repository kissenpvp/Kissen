package net.kissenpvp.base;

import net.kissenpvp.api.base.Kissen;
import net.kissenpvp.api.localization.GlobalLocaleRegistry;
import net.kissenpvp.api.network.actor.PlayerModule;
import net.kissenpvp.api.network.actor.rank.RankModule;
import net.kissenpvp.api.punishment.PunishmentModule;
import net.kissenpvp.localization.InternalGlobalLocaleRegistry;
import org.jetbrains.annotations.NotNull;

public class KissenCore implements Kissen
{
    private static Kissen instance;
    private boolean started;
    private GlobalLocaleRegistry localeRegistry;
    private PunishmentModule punishmentModule;
    private RankModule rankModule;
    private PlayerModule playerModule;

    protected void startCore(@NotNull PunishmentModule punishmentModule, @NotNull RankModule rankModule, @NotNull PlayerModule playerModule)
    {
        localeRegistry = new InternalGlobalLocaleRegistry();

        this.punishmentModule = punishmentModule;
        this.rankModule = rankModule;
        this.playerModule = playerModule;

        started = true;
    }

    @Override public @NotNull PunishmentModule punishmentModule()
    {
        return punishmentModule;
    }

    @Override public @NotNull RankModule rankModule()
    {
        return rankModule;
    }

    @Override public @NotNull PlayerModule playerModule()
    {
        return playerModule;
    }

    @Override public @NotNull GlobalLocaleRegistry localeRegistry()
    {
        return localeRegistry;
    }

    @Override public boolean started()
    {
        return started;
    }
}
