package net.kissenpvp.api.punishment;

import net.kissenpvp.api.database.SubscriptionEntity;
import net.kissenpvp.api.temporal.timespan.TimeSpan;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface PunishmentSubscription extends SubscriptionEntity<UUID, Integer, Punishment>
{
    @NotNull Instant start();

    @NotNull TimeSpan timeSpan();

    void timeSpan(@NotNull TimeSpan timeSpan);

    @NotNull Optional<Component> message();

    void message(@Nullable Component component);

    void unsetMessage();

}
