package net.kissenpvp.api.base;


import net.kissenpvp.api.database.Repository;
import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.api.network.actor.rank.Rank;
import net.kissenpvp.api.network.actor.rank.RankSubscription;
import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentSubscription;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface Kissen
{
    @NotNull Repository<Integer, Punishment> punishmentRepository();

    @NotNull Repository<String, PunishmentSubscription> punishmentSubscriptionRepository();

    @NotNull Repository<String, Rank> rankRepository();

    @NotNull Repository<String, RankSubscription> rankSubscriptionRepository();

    @NotNull Repository<UUID, PlayerClient> playerRepository();
}
