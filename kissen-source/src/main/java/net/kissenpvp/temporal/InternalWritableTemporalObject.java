package net.kissenpvp.temporal;

import net.kissenpvp.api.temporal.WritableTemporalObject;
import net.kissenpvp.api.temporal.timespan.DefinedTimeSpan;
import net.kissenpvp.api.temporal.timespan.TimeSpan;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class InternalWritableTemporalObject implements WritableTemporalObject
{
    private final @NotNull Instant start;
    private final @Nullable Instant originalExpiry;
    private @Nullable Instant expiry;

    public static @NotNull InternalTemporalObject toTemporal(@NotNull TimeSpan span)
    {
        if(span instanceof DefinedTimeSpan definedTimeSpan)
        {
            return new InternalTemporalObject(Instant.now().plus(definedTimeSpan));
        }

        return new InternalTemporalObject();
    }

    public InternalWritableTemporalObject()
    {
        this(null);
    }

    public InternalWritableTemporalObject(@Nullable Instant expiry)
    {
        start = Instant.now();
        this.expiry = expiry;
        originalExpiry = expiry;
    }

    @Override public void expire()
    {
        this.expiry = Instant.now();
    }

    @Override public @NotNull Optional<Instant> expectedExpiry()
    {
        return Optional.ofNullable(originalExpiry);
    }

    @Override public boolean expiryAltered()
    {
        return !Objects.equals(originalExpiry, expiry);
    }

    @Override public @NotNull Optional<Instant> expiry()
    {
        return Optional.ofNullable(expiry);
    }

    @Override public @NotNull Instant start()
    {
        return start;
    }
}
