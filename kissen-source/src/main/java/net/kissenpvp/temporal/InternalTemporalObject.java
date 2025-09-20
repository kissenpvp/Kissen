package net.kissenpvp.temporal;

import net.kissenpvp.api.temporal.TemporalObject;
import net.kissenpvp.api.temporal.timespan.DefinedTimeSpan;
import net.kissenpvp.api.temporal.timespan.TimeSpan;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Optional;

public record InternalTemporalObject(@NotNull Instant start, @Nullable Instant expiryTime) implements TemporalObject
{
    public static @NotNull InternalTemporalObject toTemporal(@NotNull TimeSpan span)
    {
        if(span instanceof DefinedTimeSpan definedTimeSpan)
        {
            return new InternalTemporalObject(Instant.now().plus(definedTimeSpan));
        }

        return new InternalTemporalObject();
    }

    InternalTemporalObject()
    {
        this(null);
    }

    public InternalTemporalObject(@Nullable Instant expiry)
    {
        this(Instant.now(), expiry);
    }


    @Override public @NotNull Optional<Instant> expiry()
    {
        return Optional.ofNullable(expiryTime());
    }

    @Override public boolean hasExpiry()
    {
        return expiry().isPresent() && expiry().get().isAfter(Instant.now());
    }

    @Override public @NotNull Instant start()
    {
        return start;
    }
}
