package net.kissenpvp.temporal.timespan;

import net.kissenpvp.api.temporal.timespan.DefinedTimeSpan;
import org.jspecify.annotations.NonNull;


import java.time.Duration;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.List;

public class InternalDefinedTimeSpan implements DefinedTimeSpan
{
    private final long duration;

    public InternalDefinedTimeSpan(long duration)
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