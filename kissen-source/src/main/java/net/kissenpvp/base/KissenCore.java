package net.kissenpvp.base;

import net.kissenpvp.api.base.Kissen;
import net.kissenpvp.api.database.Repository;
import net.kissenpvp.api.localization.GlobalLocaleRegistry;
import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.api.network.actor.rank.RankModule;
import net.kissenpvp.api.punishment.PunishmentModule;
import net.kissenpvp.localization.InternalGlobalLocaleRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class KissenCore implements Kissen
{
    private static Kissen instance;
    private boolean started;
    private GlobalLocaleRegistry localeRegistry;
    private PunishmentModule punishmentModule;
    private RankModule rankModule;

    public static @NotNull Kissen getInstance()
    {
        return Objects.requireNonNullElseGet(instance, () ->
        {
            instance = new KissenCore();
            return instance;
        });
    }

    public void start(@NotNull PunishmentModule punishmentModule, @NotNull RankModule rankModule)
    {
        localeRegistry = new InternalGlobalLocaleRegistry();

        this.punishmentModule = punishmentModule;
        this.rankModule = rankModule;

        started = true;
    }

    @Override public @NotNull PunishmentModule punishmentModule()
    {
        return null;
    }

    @Override public @NotNull RankModule rankModule()
    {
        return null;
    }

    @Override public @NotNull GlobalLocaleRegistry localeRegistry()
    {
        return localeRegistry;
    }

    @Override public @NotNull Repository<UUID, PlayerClient> playerRepository()
    {
        return null;
    }

    @Override public boolean started()
    {
        return started;
    }
}
