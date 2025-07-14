package net.kissenpvp.api.network.actor.rank;

import net.kissenpvp.api.database.Repository;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a component responsible for providing access to rank management functionalities.
 * This module facilitates operations related to rank definitions and rank subscriptions
 * by exposing their respective repositories.
 *
 * @author Ivo Quiring
 */
public interface RankModule
{
    /**
     * Provides access to the repository responsible for managing {@link Rank} entities.
     * The repository allows for performing CRUD operations and managing rank data
     * within the system.
     *
     * @return a {@link Repository} instance responsible for managing {@link Rank} entities;
     *         never null
     */
    @NotNull Repository<String, Rank> rankRepository();

    /**
     * Provides access to the repository responsible for managing {@link RankSubscription} entities.
     * The repository allows performing CRUD operations and managing rank subscription data
     * within the system.
     *
     * @return a {@link Repository} instance responsible for managing {@link RankSubscription} entities; never null
     */
    @NotNull Repository<String, RankSubscription> rankSubscriptionRepository();

}
