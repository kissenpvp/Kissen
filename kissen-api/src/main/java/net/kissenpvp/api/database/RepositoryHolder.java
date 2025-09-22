package net.kissenpvp.api.database;

import net.kissenpvp.api.network.actor.OperatorInfo;
import net.kissenpvp.api.network.actor.PlayerRepository;
import net.kissenpvp.api.network.actor.rank.Rank;
import net.kissenpvp.api.network.actor.rank.RankSubscription;
import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentSubscriptionRepository;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public interface RepositoryHolder
{
    @NonNull PlayerRepository playerRepository();

    @NonNull Repository<UUID, OperatorInfo> operatorRepository();

    @NonNull Repository<Integer, Punishment> punishmentRepository();

    @NonNull PunishmentSubscriptionRepository punishmentSubscriptionRepository();

    @NonNull Repository<String, Rank> rankRepository();

    @NonNull Repository<String, RankSubscription> rankSubscriptionRepository();

}
