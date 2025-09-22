package net.kissenpvp.temporal.timespan;

import net.kissenpvp.api.temporal.timespan.DefinedTimeSpan;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

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

    private @NotNull Duration duration()
    {
        return Duration.ofMillis(duration);
    }

    @Override
    public long get(@NotNull TemporalUnit unit)
    {
        return duration().get(unit);
    }

    @Override
    public @NotNull @UnmodifiableView List<TemporalUnit> getUnits()
    {
        return duration().getUnits();
    }

    @Override
    public @NotNull Temporal addTo(@NotNull Temporal temporal)
    {
        return duration().addTo(temporal);
    }

    @Override
    public @NotNull Temporal subtractFrom(@NotNull Temporal temporal)
    {
        return temporal.minus(duration());
    }
}