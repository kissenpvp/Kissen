package net.kissenpvp.base;

import net.kissenpvp.api.base.Kissen;
import net.kissenpvp.api.database.Repository;
import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.api.network.actor.rank.Rank;
import net.kissenpvp.api.network.actor.rank.RankSubscription;
import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentSubscription;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class KissenCore implements Kissen
{
    private static Kissen instance;

    public static @NotNull Kissen getInstance()
    {
        return Objects.requireNonNullElseGet(instance, () ->
        {
            instance = new KissenCore();
            return instance;
        });
    }

    @Override public @NotNull Repository<Integer, Punishment> punishmentRepository()
    {
        return null;
    }

    @Override public @NotNull Repository<String, PunishmentSubscription> punishmentSubscriptionRepository()
    {
        return null;
    }

    @Override public @NotNull Repository<String, Rank> rankRepository()
    {
        return null;
    }

    @Override public @NotNull Repository<String, RankSubscription> rankSubscriptionRepository()
    {
        return null;
    }

    @Override public @NotNull Repository<UUID, PlayerClient> playerRepository()
    {
        return null;
    }
}
