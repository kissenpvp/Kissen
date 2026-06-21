package net.kissenpvp.api.temporal;

import com.google.common.base.Preconditions;
import net.kissenpvp.api.temporal.timespan.DefinedTimeSpan;
import net.kissenpvp.api.temporal.timespan.TimeSpan;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class WritableTemporalObject implements TemporalObject
{
    private final @NonNull Instant start;
    private final @Nullable Instant originalExpiry;
    private @Nullable Instant expiry;

    public WritableTemporalObject()
    {
        this(null);
    }

    public WritableTemporalObject(@Nullable Instant expiry)
    {
        this(Instant.now(), expiry, expiry);
    }

    public WritableTemporalObject(
            @NonNull Instant start, @Nullable Instant expiry,
            @Nullable Instant expectedExpiry
    ) throws NullPointerException
    {
        Preconditions.checkNotNull(start, "Start must be not null");

        this.start = start;
        this.expiry = expiry;
        originalExpiry = expectedExpiry;
    }

    public static @NonNull WritableTemporalObject toTemporal(@NonNull TimeSpan span)
    {
        if (span instanceof DefinedTimeSpan definedTimeSpan)
        {
            return new WritableTemporalObject(Instant.now().plus(definedTimeSpan));
        }

        return new WritableTemporalObject();
    }

    public void expire()
    {
        this.expiry = Instant.now();
    }

    public @NonNull Optional<Instant> expectedExpiry()
    {
        return Optional.ofNullable(originalExpiry);
    }

    public boolean expiryAltered()
    {
        return !Objects.equals(originalExpiry, expiry);
    }

    public @NonNull Optional<Instant> expiry()
    {
        return Optional.ofNullable(expiry);
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
