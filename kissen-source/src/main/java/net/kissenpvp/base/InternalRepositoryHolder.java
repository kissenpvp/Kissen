package net.kissenpvp.base;

import net.kissenpvp.api.database.Repository;
import net.kissenpvp.api.database.RepositoryHolder;
import net.kissenpvp.api.network.actor.OperatorInfo;
import net.kissenpvp.api.network.actor.PlayerRepository;
import net.kissenpvp.api.network.actor.rank.Rank;
import net.kissenpvp.api.network.actor.rank.RankSubscription;
import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentSubscription;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public record InternalRepositoryHolder(@NotNull PlayerRepository playerRepository, @NotNull Repository<UUID, OperatorInfo> operatorRepository,
                                       @NotNull Repository<Integer, Punishment> punishmentRepository, @NotNull Repository<String, PunishmentSubscription> punishmentSubscriptionRepository,
                                       @NotNull Repository<String, Rank> rankRepository, @NotNull Repository<String, RankSubscription> rankSubscriptionRepository
) implements RepositoryHolder {

    public static RepositoryHolderBuilder builder()
    {
        return new RepositoryHolderBuilder();
    }

    public static class RepositoryHolderBuilder
    {
        private PlayerRepository playerRepository;
        private Repository<UUID, OperatorInfo> operatorRepository;
        private Repository<Integer, Punishment> punishmentRepository;
        private Repository<String, PunishmentSubscription> punishmentSubscriptionRepository;
        private Repository<String, Rank> rankRepository;
        private Repository<String, RankSubscription> rankSubscriptionRepository;

        public @NotNull InternalRepositoryHolder.RepositoryHolderBuilder playerRepository(@NotNull PlayerRepository playerRepository) throws NullPointerException
        {
            Objects.requireNonNull(playerRepository, "playerRepository cannot be null!");

            this.playerRepository = playerRepository;
            return this;
        }

        public @NotNull InternalRepositoryHolder.RepositoryHolderBuilder operatorRepository(@NotNull Repository<UUID, OperatorInfo> operatorRepository) throws NullPointerException
        {
            Objects.requireNonNull(operatorRepository, "operatorRepository cannot be null!");

            this.operatorRepository = operatorRepository;
            return this;
        }

        public @NotNull InternalRepositoryHolder.RepositoryHolderBuilder punishmentRepository(@NotNull Repository<Integer, Punishment> punishmentRepository) throws NullPointerException
        {
            Objects.requireNonNull(punishmentRepository, "punishmentRepository cannot be null!");

            this.punishmentRepository = punishmentRepository;
            return this;
        }

        public @NotNull InternalRepositoryHolder.RepositoryHolderBuilder punishmentSubscriptionRepository(@NotNull Repository<String, PunishmentSubscription> punishmentSubscriptionRepository) throws NullPointerException
        {
            Objects.requireNonNull(punishmentSubscriptionRepository, "punishmentSubscriptionRepository cannot be null!");

            this.punishmentSubscriptionRepository = punishmentSubscriptionRepository;
            return this;
        }

        public @NotNull InternalRepositoryHolder.RepositoryHolderBuilder rankRepository(@NotNull Repository<String, Rank> rankRepository) throws NullPointerException
        {
            Objects.requireNonNull(rankRepository, "rankRepository cannot be null!");

            this.rankRepository = rankRepository;
            return this;
        }

        public @NotNull InternalRepositoryHolder.RepositoryHolderBuilder rankSubscriptionRepository(@NotNull Repository<String, RankSubscription> rankSubscriptionRepository) throws NullPointerException
        {
            Objects.requireNonNull(rankSubscriptionRepository, "rankSubscriptionRepository cannot be null!");

            this.rankSubscriptionRepository = rankSubscriptionRepository;
            return this;
        }

        @Contract("-> new")
        public @NotNull InternalRepositoryHolder build()
        {
            return new InternalRepositoryHolder(playerRepository, operatorRepository, punishmentRepository, punishmentSubscriptionRepository, rankRepository, rankSubscriptionRepository);
        }
    }
}
