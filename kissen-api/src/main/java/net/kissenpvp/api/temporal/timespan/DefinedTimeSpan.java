package net.kissenpvp.api.temporal.timespan;

import org.jspecify.annotations.NonNull;


import java.time.Duration;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;

/**
 * Represents a fixed span of time.
 * <p>
 * The {@code DefinedTimeSpan} interface extends both {@link TimeSpan} and {@link TemporalAmount},
 * allowing for precise temporal manipulation and query capabilities.
 *
 * @author Ivo Quiring
 * @see TimeSpan
 * @see TemporalAmount
 */
public class DefinedTimeSpan implements TimeSpan, TemporalAmount
{
    private final long duration;

    public DefinedTimeSpan(long duration)
    {
        this.duration = duration;
    }

    private @NonNull Duration duration()
    {
        return Duration.ofMillis(duration);
    }

    @Override
    public long get(@NonNull TemporalUnit unit)
    {
        return duration().get(unit);
    }

    @Override
    public @NonNull  List<TemporalUnit> getUnits()
    {
        return duration().getUnits();
    }

    @Override
    public @NonNull Temporal addTo(@NonNull Temporal temporal)
    {
        return duration().addTo(temporal);
    }

    @Override
    public @NonNull Temporal subtractFrom(@NonNull Temporal temporal)
    {
        return temporal.minus(duration());
    }
}