package net.kissenpvp.api.database;

import net.kissenpvp.api.network.actor.OperatorInfo;
import net.kissenpvp.api.network.actor.PlayerRepository;
import net.kissenpvp.api.network.actor.rank.Rank;
import net.kissenpvp.api.network.actor.rank.RankSubscription;
import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentSubscription;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface RepositoryHolder
{
    @NotNull PlayerRepository playerRepository();

    @NotNull Repository<UUID, OperatorInfo> operatorRepository();

    @NotNull Repository<Integer, Punishment> punishmentRepository();

    @NotNull Repository<String, PunishmentSubscription> punishmentSubscriptionRepository();

    @NotNull Repository<String, Rank> rankRepository();

    @NotNull Repository<String, RankSubscription> rankSubscriptionRepository();

}
