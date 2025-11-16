package net.kissenpvp.api.database;

import net.kissenpvp.api.network.actor.PlayerRepository;
import net.kissenpvp.api.network.actor.rank.Rank;
import net.kissenpvp.api.network.actor.rank.RankSubscriptionRepository;
import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentSubscriptionRepository;
import org.jspecify.annotations.NonNull;

public interface RepositoryHolder
{
    @NonNull PlayerRepository playerRepository();

    @NonNull Repository<Integer, Punishment> punishmentRepository();

    @NonNull PunishmentSubscriptionRepository punishmentSubscriptionRepository();

    @NonNull Repository<String, Rank> rankRepository();

    @NonNull RankSubscriptionRepository rankSubscriptionRepository();

}
