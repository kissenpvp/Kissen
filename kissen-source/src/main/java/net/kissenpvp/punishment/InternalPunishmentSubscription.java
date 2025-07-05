package net.kissenpvp.punishment;

import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentSubscription;
import net.kissenpvp.api.temporal.timespan.TimeSpan;
import net.kissenpvp.database.InternalPersistableEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class InternalPunishmentSubscription extends InternalPersistableEntity<UUID> implements PunishmentSubscription
{
    private final UUID linkId;
    private final Integer parent;
    private final Integer parentSignature;
    private @NotNull TimeSpan timeSpan;
    private @Nullable Component message;

    public InternalPunishmentSubscription(@NotNull UUID linkId, @NotNull Integer parent, @NotNull Integer parentSignature, @NotNull TimeSpan timeSpan, @Nullable Component message) throws NullPointerException
    {
        Objects.requireNonNull(linkId, "LinkId cannot be null!");
        Objects.requireNonNull(parent, "Parent cannot be null!");
        Objects.requireNonNull(parentSignature, "ParentSignature cannot be null!");
        Objects.requireNonNull(timeSpan, "TimeSpan cannot be null!");

        this.linkId = linkId;
        this.parent = parent;
        this.parentSignature = parentSignature;
        this.timeSpan = timeSpan;
        this.message = message;
    }

    @Override public @NotNull UUID id()
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
