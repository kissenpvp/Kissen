package net.kissenpvp.punishment;

import net.kissenpvp.api.database.Repository;
import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentModule;
import net.kissenpvp.api.punishment.PunishmentSubscription;
import net.kissenpvp.api.temporal.WritableTemporalObject;
import net.kissenpvp.temporal.InternalWritableTemporalObject;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Objects;

public abstract class InternalPunishmentModule implements PunishmentModule
{
    private final @NotNull Repository<Integer, Punishment> punishmentRepository;
    private final @NotNull Repository<String, PunishmentSubscription> punishmentSubscriptionRepository;

    public InternalPunishmentModule(@NotNull Repository<Integer, Punishment> punishmentRepository, @NotNull Repository<String, PunishmentSubscription> punishmentSubscriptionRepository)
    {
        this.punishmentRepository = punishmentRepository;
        this.punishmentSubscriptionRepository = punishmentSubscriptionRepository;
    }


    @Override public void punishPlayer(@NotNull PlayerClient player, @NotNull Punishment punishment)
    {
        punishPlayer(player, punishment, null);
    }

    @Override
    public void punishPlayer(@NotNull PlayerClient player, @NotNull Punishment punishment, @Nullable Component message)
    {
        WritableTemporalObject temporalObject = InternalWritableTemporalObject.toTemporal(punishment.timeSpan());

        if (Objects.isNull(message))
        {
            message = punishment.defaultMessage().orElse(null);
        }

        PunishmentSubscription subscription = new InternalPunishmentSubscription(punishment.id(), player.linkId(), temporalObject, message);

        punishmentSubscriptionRepository().save(subscription).join();
    }

    public abstract void executePunishment(@NotNull PunishmentSubscription subscription);

    @Override public @NotNull Repository<Integer, Punishment> punishmentRepository() { return punishmentRepository; }

    @Override public @NotNull Repository<String, PunishmentSubscription> punishmentSubscriptionRepository() { return punishmentSubscriptionRepository; }
}
