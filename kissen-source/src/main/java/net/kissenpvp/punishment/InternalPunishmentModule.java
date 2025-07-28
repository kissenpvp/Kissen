package net.kissenpvp.punishment;

import net.kissenpvp.api.database.Repository;
import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentModule;
import net.kissenpvp.api.punishment.PunishmentSubscription;
import org.jetbrains.annotations.NotNull;

public record InternalPunishmentModule(@NotNull Repository<Integer, Punishment> punishmentRepository, @NotNull Repository<String, PunishmentSubscription> punishmentSubscriptionRepository) implements PunishmentModule {}
