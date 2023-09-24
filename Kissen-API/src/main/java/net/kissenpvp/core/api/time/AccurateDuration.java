package net.kissenpvp.core.api.time;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Period;

public interface AccurateDuration {

    @NotNull Period getPeriod();

    @NotNull Duration getDuration();

    long getMillis();
}
