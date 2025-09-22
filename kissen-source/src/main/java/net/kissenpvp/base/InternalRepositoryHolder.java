package net.kissenpvp.base;

import net.kissenpvp.api.database.Repository;
import net.kissenpvp.api.database.RepositoryHolder;
import net.kissenpvp.api.network.actor.OperatorInfo;
import net.kissenpvp.api.network.actor.PlayerRepository;
import net.kissenpvp.api.network.actor.rank.Rank;
import net.kissenpvp.api.network.actor.rank.RankSubscription;
import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentSubscriptionRepository;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.UUID;

public record InternalRepositoryHolder(@NonNull PlayerRepository playerRepository,
                                       @NonNull Repository<UUID, OperatorInfo> operatorRepository,
                                       @NonNull Repository<Integer, Punishment> punishmentRepository,
                                       @NonNull PunishmentSubscriptionRepository punishmentSubscriptionRepository,
                                       @NonNull Repository<String, Rank> rankRepository,
                                       @NonNull Repository<String, RankSubscription> rankSubscriptionRepository
) implements RepositoryHolder
{

    public InternalRepositoryHolder(
            @NonNull PlayerRepository playerRepository, @NonNull Repository<UUID,
                    OperatorInfo> operatorRepository, @NonNull Repository<Integer, Punishment> punishmentRepository,
            @NonNull PunishmentSubscriptionRepository punishmentSubscriptionRepository,
            @NonNull Repository<String, Rank> rankRepository, @NonNull Repository<String,
                    RankSubscription> rankSubscriptionRepository
    )
    {
        this.playerRepository = playerRepository;
        this.operatorRepository = operatorRepository;
        this.punishmentRepository = punishmentRepository;
        this.punishmentSubscriptionRepository = punishmentSubscriptionRepository;
        this.rankRepository = rankRepository;
        this.rankSubscriptionRepository = rankSubscriptionRepository;

        Objects.requireNonNull(playerRepository, "playerRepository cannot be null!");
        Objects.requireNonNull(operatorRepository, "operatorRepository cannot be null!");
        Objects.requireNonNull(punishmentRepository, "punishmentRepository cannot be null!");
        Objects.requireNonNull(punishmentSubscriptionRepository, "punishmentSubscriptionRepository cannot be null!");
        Objects.requireNonNull(rankRepository, "rankRepository cannot be null!");
        Objects.requireNonNull(rankSubscriptionRepository, "rankSubscriptionRepository cannot be null!");
    }

    public static RepositoryHolderBuilder builder()
    {
        return new RepositoryHolderBuilder();
    }

    public static class RepositoryHolderBuilder
    {
        private PlayerRepository playerRepository;
        private Repository<UUID, OperatorInfo> operatorRepository;
        private Repository<Integer, Punishment> punishmentRepository;
        private PunishmentSubscriptionRepository punishmentSubscriptionRepository;
        private Repository<String, Rank> rankRepository;
        private Repository<String, RankSubscription> rankSubscriptionRepository;

        public InternalRepositoryHolder.@NonNull RepositoryHolderBuilder playerRepository(@NonNull PlayerRepository playerRepository) throws NullPointerException
        {
            Objects.requireNonNull(playerRepository, "playerRepository cannot be null!");

            this.playerRepository = playerRepository;
            return this;
        }

        public InternalRepositoryHolder.@NonNull RepositoryHolderBuilder operatorRepository(
                @NonNull Repository<UUID,
                        OperatorInfo> operatorRepository
        ) throws NullPointerException
        {
            Objects.requireNonNull(operatorRepository, "operatorRepository cannot be null!");

            this.operatorRepository = operatorRepository;
            return this;
        }

        public InternalRepositoryHolder.@NonNull RepositoryHolderBuilder punishmentRepository(@NonNull Repository<Integer, Punishment> punishmentRepository) throws NullPointerException
        {
            Objects.requireNonNull(punishmentRepository, "punishmentRepository cannot be null!");

            this.punishmentRepository = punishmentRepository;
            return this;
        }

        public InternalRepositoryHolder.@NonNull RepositoryHolderBuilder punishmentSubscriptionRepository(@NonNull PunishmentSubscriptionRepository punishmentSubscriptionRepository) throws NullPointerException
        {
            Objects.requireNonNull(punishmentSubscriptionRepository, "punishmentSubscriptionRepository cannot be " +
                    "null!");

            this.punishmentSubscriptionRepository = punishmentSubscriptionRepository;
            return this;
        }

        public InternalRepositoryHolder.@NonNull RepositoryHolderBuilder rankRepository(
                @NonNull Repository<String,
                        Rank> rankRepository
        ) throws NullPointerException
        {
            Objects.requireNonNull(rankRepository, "rankRepository cannot be null!");

            this.rankRepository = rankRepository;
            return this;
        }

        public InternalRepositoryHolder.@NonNull RepositoryHolderBuilder rankSubscriptionRepository(@NonNull Repository<String, RankSubscription> rankSubscriptionRepository) throws NullPointerException
        {
            Objects.requireNonNull(rankSubscriptionRepository, "rankSubscriptionRepository cannot be null!");

            this.rankSubscriptionRepository = rankSubscriptionRepository;
            return this;
        }

        public @NonNull InternalRepositoryHolder build()
        {
            return new InternalRepositoryHolder(playerRepository, operatorRepository, punishmentRepository,
                    punishmentSubscriptionRepository, rankRepository, rankSubscriptionRepository);
        }
    }
}
