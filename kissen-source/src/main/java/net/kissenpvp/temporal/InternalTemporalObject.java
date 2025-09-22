package net.kissenpvp.temporal;

import net.kissenpvp.api.temporal.TemporalObject;
import net.kissenpvp.api.temporal.timespan.DefinedTimeSpan;
import net.kissenpvp.api.temporal.timespan.TimeSpan;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Optional;

public record InternalTemporalObject(@NonNull Instant start, @Nullable Instant expiryTime) implements TemporalObject
{
    InternalTemporalObject()
    {
        this(null);
    }

    public InternalTemporalObject(@Nullable Instant expiry)
    {
        this(Instant.now(), expiry);
    }

    public static @NonNull InternalTemporalObject toTemporal(@NonNull TimeSpan span)
    {
        if (span instanceof DefinedTimeSpan definedTimeSpan)
        {
            return new InternalTemporalObject(Instant.now().plus(definedTimeSpan));
        }

        return new InternalTemporalObject();
    }

    @Override public @NonNull Optional<Instant> expiry()
    {
        return Optional.ofNullable(expiryTime());
    }

    @Override public boolean expired()
    {
        return expiry().isPresent() && expiry().get().isBefore(Instant.now());
    }

    @Override public @NonNull Instant start()
    {
        return start;
    }
}
