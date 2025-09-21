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

    public static @NotNull InternalWritableTemporalObject toTemporal(@NotNull TimeSpan span)
    {
        if(span instanceof DefinedTimeSpan definedTimeSpan)
        {
            return new InternalWritableTemporalObject(Instant.now().plus(definedTimeSpan));
        }

        return new InternalWritableTemporalObject();
    }

    public InternalWritableTemporalObject()
    {
        this(null);
    }

    public InternalWritableTemporalObject(@Nullable Instant expiry)
    {
        this(Instant.now(), expiry, expiry);
    }

    public InternalWritableTemporalObject(@NotNull Instant start, @Nullable Instant expiry, @Nullable Instant expectedExpiry) throws NullPointerException
    {
        Objects.requireNonNull(start, "Start must be not null");

        this.start = start;
        this.expiry = expiry;
        originalExpiry = expectedExpiry;
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

    @Override public boolean expired()
    {
        return expiry().isPresent() && expiry().get().isBefore(Instant.now());
    }

    @Override public @NotNull Instant start()
    {
        return start;
    }
}
