package net.kissenpvp.punishment;

import com.google.common.base.Preconditions;
import net.kissenpvp.api.database.PersistableEntity;
import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentType;
import net.kissenpvp.api.temporal.timespan.TimeSpan;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class InternalPunishment implements PersistableEntity<Integer>, Punishment
{
    private final Integer id;
    private PunishmentType punishmentType;
    private TimeSpan timeSpan;
    private @Nullable Component defaultMessage;

    public InternalPunishment(int id, @NonNull PunishmentType punishmentType, @NonNull TimeSpan timeSpan)
    {
        this(id, punishmentType, timeSpan, null);
    }

    public InternalPunishment(
            int id, @NonNull PunishmentType punishmentType, @NonNull TimeSpan timeSpan,
            @Nullable Component defaultMessage
    ) {
        Preconditions.checkNotNull(punishmentType, "PunishmentType cannot be null.");
        Preconditions.checkNotNull(timeSpan, "TimeSpan cannot be null.");

        this.id = id;
        this.punishmentType = punishmentType;
        this.timeSpan = timeSpan;
        this.defaultMessage = defaultMessage;
    }

    @Override public @NonNull Integer id()
    {
        return id;
    }

    @Override public @NonNull TimeSpan timeSpan()
    {
        return timeSpan;
    }

    @Override public void timeSpan(@NonNull TimeSpan timeSpan)
    {
        Preconditions.checkNotNull(timeSpan, "TimeSpan cannot be null.");
        this.timeSpan = timeSpan;
    }

    @Override public @NonNull PunishmentType punishmentType()
    {
        return punishmentType;
    }

    @Override public void punishmentType(@NonNull PunishmentType punishmentType)
    {
        Preconditions.checkNotNull(punishmentType, "PunishmentType cannot be null.");
        this.punishmentType = punishmentType;
    }

    @Override public @NonNull Optional<Component> defaultMessage()
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
        if (o == null || getClass() != o.getClass()) { return false; }
        InternalPunishment that = (InternalPunishment) o;
        return Objects.equals(id, that.id);
    }

    @Override public int hashCode()
    {
        return Objects.hashCode(id);
    }
}
