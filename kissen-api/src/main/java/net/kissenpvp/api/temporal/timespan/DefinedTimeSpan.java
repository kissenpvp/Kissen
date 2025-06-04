package net.kissenpvp.api.temporal.timespan;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.TemporalAmount;

public interface DefinedTimeSpan extends TimeSpan, TemporalAmount
{
    @NotNull Period period();

    @NotNull Duration duration();
}
