package net.kissenpvp.api.punishment;

import net.kissenpvp.api.database.Repository;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code PunishmentModule} interface provides a contract for managing
 * punishment-related functionalities. It allows clients to access repositories
 * for managing {@link Punishment} and {@link PunishmentSubscription} entities.
 *
 * @author Ivo Quiring
 */
public interface PunishmentModule
{
    /**
     * Provides access to the repository managing {@link Punishment} entities.
     * This repository is responsible for handling CRUD operations and entity management
     * for punishments, where an Integer key identifies each punishment.
     *
     * @return a {@link Repository} instance managing {@link Punishment} entities,
     *         where the keys are of type {@link Integer}.
     */
    @NotNull Repository<Integer, Punishment> punishmentRepository();

    /**
     * Provides access to the repository managing {@link PunishmentSubscription} entities.
     * This repository is responsible for handling CRUD operations and entity management
     * for punishment subscriptions, where a String key identifies each subscription.
     *
     * @return a {@link Repository} instance managing {@link PunishmentSubscription} entities,
     *         where the keys are of type {@link String}.
     */
    @NotNull Repository<String, PunishmentSubscription> punishmentSubscriptionRepository();
}
