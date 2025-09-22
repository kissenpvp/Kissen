package net.kissenpvp.base;

import net.kissenpvp.api.base.Kissen;
import net.kissenpvp.api.database.Repository;
import net.kissenpvp.api.database.RepositoryHolder;
import net.kissenpvp.api.localization.GlobalLocaleRegistry;
import net.kissenpvp.api.network.actor.ConsoleClient;
import net.kissenpvp.api.network.actor.OperatorInfo;
import net.kissenpvp.api.network.actor.PlayerRepository;
import net.kissenpvp.api.network.actor.rank.Rank;
import net.kissenpvp.api.network.actor.rank.RankSubscription;
import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentSubscriptionRepository;
import net.kissenpvp.localization.InternalGlobalLocaleRegistry;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.UUID;

public abstract class KissenCore implements Kissen, RepositoryHolder
{
    private static KissenCore instance;
    private GlobalLocaleRegistry localeRegistry;
    private InternalRepositoryHolder databaseModule;
    private boolean started;

    public static @NonNull KissenCore getInstance()
    {
        if (Objects.isNull(instance))
        {
            throw new IllegalStateException("Kissen has not been initiated yet!");
        }
        return instance;
    }

    public abstract ConsoleClient console();

    protected void databaseModule(@NonNull InternalRepositoryHolder databaseModule)
    {
        Objects.requireNonNull(databaseModule.playerRepository(), "playerRepository cannot be null!");
        Objects.requireNonNull(databaseModule.operatorRepository(), "operatorRepository cannot be null!");
        Objects.requireNonNull(databaseModule.punishmentRepository(), "punishmentRepository cannot be null!");
        Objects.requireNonNull(databaseModule.punishmentSubscriptionRepository(), "punishmentSubscriptionRepository " +
                "cannot be null!");
        Objects.requireNonNull(databaseModule.rankRepository(), "rankRepository cannot be null!");
        Objects.requireNonNull(databaseModule.rankSubscriptionRepository(), "rankSubscriptionRepository cannot be " +
                "null!");

        this.databaseModule = databaseModule;
    }

    protected void init()
    {
        if (!Objects.nonNull(databaseModule))
        {
            throw new IllegalStateException("Cannot start Kissen without a database!");
        }

        instance = this;
        localeRegistry = new InternalGlobalLocaleRegistry();

        started = true;
    }

    @Override public @NonNull GlobalLocaleRegistry localeRegistry()
    {
        return localeRegistry;
    }

    @Override public boolean started()
    {
        return started;
    }

    @Override public @NonNull PlayerRepository playerRepository()
    {
        return databaseModule.playerRepository();
    }

    @Override public @NonNull Repository<UUID, OperatorInfo> operatorRepository()
    {
        return databaseModule.operatorRepository();
    }

    @Override public @NonNull Repository<Integer, Punishment> punishmentRepository()
    {
        return databaseModule.punishmentRepository();
    }

    @Override public @NonNull PunishmentSubscriptionRepository punishmentSubscriptionRepository()
    {
        return databaseModule.punishmentSubscriptionRepository();
    }

    @Override public @NonNull Repository<String, Rank> rankRepository()
    {
        return databaseModule.rankRepository();
    }

    @Override public @NonNull Repository<String, RankSubscription> rankSubscriptionRepository()
    {
        return databaseModule.rankSubscriptionRepository();
    }
}
