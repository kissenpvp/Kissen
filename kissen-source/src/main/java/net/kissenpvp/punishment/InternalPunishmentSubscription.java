package net.kissenpvp.punishment;

import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentModule;
import net.kissenpvp.api.punishment.PunishmentSubscription;
import net.kissenpvp.api.temporal.timespan.TimeSpan;
import net.kissenpvp.base.KissenCore;
import net.kissenpvp.database.InternalSubscriptionEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class InternalPunishmentSubscription extends InternalSubscriptionEntity<String, Integer, Punishment> implements PunishmentSubscription
{
    private final String id;
    private final UUID linkId;
    private final Instant start;
    private @NotNull TimeSpan timeSpan;
    private @Nullable Component message;

    public InternalPunishmentSubscription(int parent, @NotNull UUID linkId, @NotNull TimeSpan timeSpan, @Nullable Component message) throws NullPointerException
    {
        this(String.valueOf(UUID.randomUUID()).split("-")[0], parent, linkId, Instant.now(), timeSpan, message);
    }

    public InternalPunishmentSubscription(@NotNull String id, int parent, @NotNull UUID linkId, @NotNull Instant start, @NotNull TimeSpan timeSpan, @Nullable Component message) throws NullPointerException
    {
        super(id, parent);
        Objects.requireNonNull(id, "Id cannot be null!");
        Objects.requireNonNull(linkId, "LinkId cannot be null!");
        Objects.requireNonNull(start, "Start cannot be null!");
        Objects.requireNonNull(timeSpan, "TimeSpan cannot be null!");

        if (id.length() > 8)
        {
            throw new IllegalArgumentException("Id cannot be longer than 4 characters!");
        }

        this.id = id;
        this.linkId = linkId;
        this.start = start;
        this.timeSpan = timeSpan;
        this.message = message;
    }

    @Override public @NotNull String id()
    {
        return id;
    }

    @Override public @NotNull UUID linkId()
    {
        return linkId;
    }

    @Override public int signature()
    {
        return Objects.hash(linkId, timeSpan, message);
    }

    @Override public @NotNull Optional<Punishment> parent()
    {
        PunishmentModule module = KissenCore.getInstance().punishmentModule();
        return Optional.ofNullable(module.punishmentRepository().find(parentId()).join());
    }

    @Override public @NotNull Instant start()
    {
        return start;
    }

    @Override public @NotNull TimeSpan timeSpan()
    {
        return timeSpan;
    }

    @Override public void timeSpan(@NotNull TimeSpan timeSpan)
    {
        this.timeSpan = timeSpan;
    }

    @Override public @NotNull Optional<Component> message()
    {
        return Optional.ofNullable(message);
    }

    @Override public void message(@Nullable Component component)
    {
        this.message = component;
    }

    @Override public void unsetMessage()
    {
        message(null);
    }
}
