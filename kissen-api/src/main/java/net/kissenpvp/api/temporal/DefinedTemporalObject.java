package net.kissenpvp.api.temporal;

import net.kissenpvp.api.temporal.timespan.DefinedTimeSpan;
import net.kissenpvp.api.temporal.timespan.TimeSpan;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Optional;

public record DefinedTemporalObject(@NonNull Instant start, @Nullable Instant expiryTime) implements TemporalObject
{
    DefinedTemporalObject()
    {
        this(null);
    }

    public DefinedTemporalObject(@Nullable Instant expiry)
    {
        this(Instant.now(), expiry);
    }

    public static @NonNull DefinedTemporalObject toTemporal(@NonNull TimeSpan span)
    {
        if (span instanceof DefinedTimeSpan definedTimeSpan)
        {
            return new DefinedTemporalObject(Instant.now().plus(definedTimeSpan));
        }

        return new DefinedTemporalObject();
    }

    public @NonNull Optional<Instant> expiry()
    {
        return Optional.ofNullable(expiryTime());
    }

    public boolean expired()
    {
        return expiry().isPresent() && expiry().get().isBefore(Instant.now());
    }

    public @NonNull Instant start()
    {
        return start;
    }
}
