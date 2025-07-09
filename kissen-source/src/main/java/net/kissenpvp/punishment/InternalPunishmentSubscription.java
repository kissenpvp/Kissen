package net.kissenpvp.punishment;

import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentSubscription;
import net.kissenpvp.api.temporal.timespan.TimeSpan;
import net.kissenpvp.database.InternalPersistableEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class InternalPunishmentSubscription extends InternalPersistableEntity<String> implements PunishmentSubscription
{
    private final String id;
    private final UUID linkId;
    private final Integer parent;
    private final Integer parentSignature;
    private final Instant start;
    private @NotNull TimeSpan timeSpan;
    private @Nullable Component message;

    public InternalPunishmentSubscription(@NotNull UUID linkId, @NotNull Integer parent, @NotNull Integer parentSignature, @NotNull TimeSpan timeSpan, @Nullable Component message) throws NullPointerException
    {
        this(String.valueOf(UUID.randomUUID()).split("-")[0], // Generate random 8 id
                linkId, parent, parentSignature, Instant.now(), timeSpan, message);
    }

    public InternalPunishmentSubscription(@NotNull String id, @NotNull UUID linkId, @NotNull Integer parent, @NotNull Integer parentSignature, @NotNull Instant start, @NotNull TimeSpan timeSpan, @Nullable Component message) throws NullPointerException
    {
        Objects.requireNonNull(id, "Id cannot be null!");
        Objects.requireNonNull(linkId, "LinkId cannot be null!");
        Objects.requireNonNull(parent, "Parent cannot be null!");
        Objects.requireNonNull(parentSignature, "ParentSignature cannot be null!");
        Objects.requireNonNull(start, "Start cannot be null!");
        Objects.requireNonNull(timeSpan, "TimeSpan cannot be null!");

        if (id.length() > 8)
        {
            throw new IllegalArgumentException("Id cannot be longer than 4 characters!");
        }

        this.id = id;
        this.linkId = linkId;
        this.parent = parent;
        this.parentSignature = parentSignature;
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
        return Objects.hash(linkId, parent, parentSignature);
    }

    @Override public @NotNull Optional<Punishment> parent()
    {
        //TODO: Implement function to retrieve punishments by their id
        return null;
    }

    @Override public @NotNull Integer parentId()
    {
        return parent;
    }

    @Override public int parentSignature()
    {
        return parentSignature;
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
