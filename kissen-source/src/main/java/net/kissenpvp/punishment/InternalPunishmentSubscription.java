package net.kissenpvp.punishment;

import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentModule;
import net.kissenpvp.api.punishment.PunishmentSubscription;
import net.kissenpvp.api.temporal.TemporalObject;
import net.kissenpvp.api.temporal.WritableTemporalObject;
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
    private final @NotNull WritableTemporalObject temporalObject;
    private @Nullable Component message;

    public InternalPunishmentSubscription(int parent, @NotNull UUID linkId, @NotNull WritableTemporalObject temporalObject, @Nullable Component message) throws NullPointerException
    {
        this(String.valueOf(UUID.randomUUID()).split("-")[0], parent, linkId, temporalObject, message);
    }

    public InternalPunishmentSubscription(@NotNull String id, int parent, @NotNull UUID linkId, @NotNull WritableTemporalObject temporalObject, @Nullable Component message) throws NullPointerException
    {
        super(id, parent);
        Objects.requireNonNull(id, "Id cannot be null.");
        Objects.requireNonNull(linkId, "LinkId cannot be null.");
        Objects.requireNonNull(temporalObject, "TimeSpan cannot be null.");

        if (id.length() > 8)
        {
            throw new IllegalArgumentException("Id cannot be longer than 4 characters!");
        }

        this.id = id;
        this.linkId = linkId;
        this.temporalObject = temporalObject;
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
        return Objects.hash(linkId, temporalObject, message);
    }

    @Override public @NotNull Optional<Punishment> parent()
    {
        PunishmentModule module = KissenCore.getInstance().punishmentModule();
        return Optional.ofNullable(module.punishmentRepository().find(parentId()).join());
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

    @Override
    public @NotNull WritableTemporalObject temporal() {
        return temporalObject;
    }
}
