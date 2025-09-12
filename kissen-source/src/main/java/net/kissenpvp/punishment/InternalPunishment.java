package net.kissenpvp.punishment;

import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentType;
import net.kissenpvp.api.temporal.timespan.TimeSpan;
import net.kissenpvp.database.InternalPersistableEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class InternalPunishment extends InternalPersistableEntity<Integer> implements Punishment
{
    private final Integer id;
    private PunishmentType punishmentType;
    private TimeSpan timeSpan;
    private @Nullable Component defaultMessage;

    public InternalPunishment(int id, @NotNull PunishmentType punishmentType, @NotNull TimeSpan timeSpan)
    {
        this(id, punishmentType, timeSpan, null);
    }

    public InternalPunishment(int id, @NotNull PunishmentType punishmentType, @NotNull TimeSpan timeSpan, @Nullable Component defaultMessage)
    {
        Objects.requireNonNull(punishmentType, "PunishmentType cannot be null.");
        Objects.requireNonNull(timeSpan, "TimeSpan cannot be null.");

        this.id = id;
        this.punishmentType = punishmentType;
        this.timeSpan = timeSpan;
        this.defaultMessage = defaultMessage;

        overrideSignature();
    }

    @Override public @NotNull Integer id()
    {
        return id;
    }

    @Override public int signature()
    {
        return Objects.hash(punishmentType, timeSpan, defaultMessage);
    }

    @Override public @NotNull TimeSpan timeSpan()
    {
        return timeSpan;
    }

    @Override public void timeSpan(@NotNull TimeSpan timeSpan)
    {
        Objects.requireNonNull(timeSpan, "TimeSpan cannot be null.");
        this.timeSpan = timeSpan;
    }

    @Override public @NotNull PunishmentType punishmentType()
    {
        return punishmentType;
    }

    @Override public void punishmentType(@NotNull PunishmentType punishmentType)
    {
        Objects.requireNonNull(punishmentType, "PunishmentType cannot be null.");
        this.punishmentType = punishmentType;
    }

    @Override public @NotNull Optional<Component> defaultMessage()
    {
        return Optional.ofNullable(this.defaultMessage);
    }

    @Override public void defaultMessage(@Nullable Component defaultMessage)
    {
        this.defaultMessage = defaultMessage;
    }

    @Override public void unsetMessage()
    {
        defaultMessage(null);
    }

    @Override public boolean equals(Object o)
    {
        if (o == null || getClass() != o.getClass()) {return false;}
        InternalPunishment that = (InternalPunishment) o;
        return Objects.equals(id, that.id);
    }

    @Override public int hashCode()
    {
        return Objects.hashCode(id);
    }
}
