package net.kissenpvp.temporal.timespan;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.List;

public class InternalDefinedTimeSpan implements net.kissenpvp.api.temporal.timespan.DefinedTimeSpan
{
    private final long duration;

    public InternalDefinedTimeSpan(long duration) {
        this.duration = duration;
    }

    @Override
    public @NotNull Period period() {
        return Period.ofDays((int) (duration / (24 * 60 * 60 * 1000)));
    }

    @Override
    public @NotNull Duration duration() {
        return Duration.ofMillis(duration);
    }

    @Override
    public long get(@NotNull TemporalUnit unit) {
        return duration().get(unit);
    }

    @Override
    public List<TemporalUnit> getUnits() {
        return duration().getUnits();
    }

    @Override
    public Temporal addTo(@NotNull Temporal temporal) {
        return duration().addTo(temporal);
    }

    @Override
    public Temporal subtractFrom(@NotNull Temporal temporal) {
        return temporal.minus(duration());
    }
}