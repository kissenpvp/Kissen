package net.kissenpvp.base;

import net.kissenpvp.api.database.Repository;
import net.kissenpvp.api.database.RepositoryHolder;
import net.kissenpvp.api.network.actor.OperatorInfo;
import net.kissenpvp.api.network.actor.PlayerRepository;
import net.kissenpvp.api.network.actor.rank.Rank;
import net.kissenpvp.api.network.actor.rank.RankSubscriptionRepository;
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
                                       @NonNull RankSubscriptionRepository rankSubscriptionRepository
) implements RepositoryHolder
{

    public InternalRepositoryHolder(
            @NonNull PlayerRepository playerRepository, @NonNull Repository<UUID,
                    OperatorInfo> operatorRepository, @NonNull Repository<Integer, Punishment> punishmentRepository,
            @NonNull PunishmentSubscriptionRepository punishmentSubscriptionRepository,
            @NonNull Repository<String, Rank> rankRepository, @NonNull RankSubscriptionRepository rankSubscriptionRepository
    )
    {
        this.playerRepository = playerRepository;
        this.operatorRepository = operatorRepository;
        this.punishmentRepository = punishmentRepository;
        this.punishmentSubscriptionRepository = punishmentSubscriptionRepository;
        this.rankRepository = rankRepository;
        this.rankSubscriptionRepository = rankSubscriptionRepository;

        Preconditions.checkNotNull(playerRepository, "playerRepository cannot be null!");
        Preconditions.checkNotNull(operatorRepository, "operatorRepository cannot be null!");
        Preconditions.checkNotNull(punishmentRepository, "punishmentRepository cannot be null!");
        Preconditions.checkNotNull(punishmentSubscriptionRepository, "punishmentSubscriptionRepository cannot be null!");
        Preconditions.checkNotNull(rankRepository, "rankRepository cannot be null!");
        Preconditions.checkNotNull(rankSubscriptionRepository, "rankSubscriptionRepository cannot be null!");
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
        private RankSubscriptionRepository rankSubscriptionRepository;

        public InternalRepositoryHolder.@NonNull RepositoryHolderBuilder playerRepository(@NonNull PlayerRepository playerRepository) throws NullPointerException
        {
            Preconditions.checkNotNull(playerRepository, "playerRepository cannot be null!");

            this.playerRepository = playerRepository;
            return this;
        }

        public InternalRepositoryHolder.@NonNull RepositoryHolderBuilder operatorRepository(
                @NonNull Repository<UUID,
                        OperatorInfo> operatorRepository
        ) throws NullPointerException
        {
            Preconditions.checkNotNull(operatorRepository, "operatorRepository cannot be null!");

            this.operatorRepository = operatorRepository;
            return this;
        }

        public InternalRepositoryHolder.@NonNull RepositoryHolderBuilder punishmentRepository(@NonNull Repository<Integer, Punishment> punishmentRepository) throws NullPointerException
        {
            Preconditions.checkNotNull(punishmentRepository, "punishmentRepository cannot be null!");

            this.punishmentRepository = punishmentRepository;
            return this;
        }

        public InternalRepositoryHolder.@NonNull RepositoryHolderBuilder punishmentSubscriptionRepository(@NonNull PunishmentSubscriptionRepository punishmentSubscriptionRepository) throws NullPointerException
        {
            Preconditions.checkNotNull(punishmentSubscriptionRepository, "punishmentSubscriptionRepository cannot be " +
                    "null!");

            this.punishmentSubscriptionRepository = punishmentSubscriptionRepository;
            return this;
        }

        public InternalRepositoryHolder.@NonNull RepositoryHolderBuilder rankRepository(
                @NonNull Repository<String,
                        Rank> rankRepository
        ) throws NullPointerException
        {
            Preconditions.checkNotNull(rankRepository, "rankRepository cannot be null!");

            this.rankRepository = rankRepository;
            return this;
        }

        public InternalRepositoryHolder.@NonNull RepositoryHolderBuilder rankSubscriptionRepository(@NonNull RankSubscriptionRepository rankSubscriptionRepository) throws NullPointerException
        {
            Preconditions.checkNotNull(rankSubscriptionRepository, "rankSubscriptionRepository cannot be null!");

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
