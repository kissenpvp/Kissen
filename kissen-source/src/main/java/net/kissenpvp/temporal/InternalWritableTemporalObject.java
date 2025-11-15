package net.kissenpvp.temporal;

import com.google.common.base.Preconditions;
import net.kissenpvp.api.temporal.WritableTemporalObject;
import net.kissenpvp.api.temporal.timespan.DefinedTimeSpan;
import net.kissenpvp.api.temporal.timespan.TimeSpan;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class InternalWritableTemporalObject implements WritableTemporalObject
{
    private final @NonNull Instant start;
    private final @Nullable Instant originalExpiry;
    private @Nullable Instant expiry;

    public InternalWritableTemporalObject()
    {
        this(null);
    }

    public InternalWritableTemporalObject(@Nullable Instant expiry)
    {
        this(Instant.now(), expiry, expiry);
    }

    public InternalWritableTemporalObject(
            @NonNull Instant start, @Nullable Instant expiry,
            @Nullable Instant expectedExpiry
    ) throws NullPointerException
    {
        Preconditions.checkNotNull(start, "Start must be not null");

        this.start = start;
        this.expiry = expiry;
        originalExpiry = expectedExpiry;
    }

    public static @NonNull InternalWritableTemporalObject toTemporal(@NonNull TimeSpan span)
    {
        if (span instanceof DefinedTimeSpan definedTimeSpan)
        {
            return new InternalWritableTemporalObject(Instant.now().plus(definedTimeSpan));
        }

        return new InternalWritableTemporalObject();
    }

    @Override public void expire()
    {
        this.expiry = Instant.now();
    }

    @Override public @NonNull Optional<Instant> expectedExpiry()
    {
        return Optional.ofNullable(originalExpiry);
    }

    @Override public boolean expiryAltered()
    {
        return !Objects.equals(originalExpiry, expiry);
    }

    @Override public @NonNull Optional<Instant> expiry()
    {
        return Optional.ofNullable(expiry);
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
