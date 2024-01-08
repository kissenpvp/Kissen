package net.kissenpvp.core.api.time;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.time.Period;
import java.util.Locale;

public interface AccurateDuration {

    @NotNull Period getPeriod();

    @NotNull Duration getDuration();

    long getMillis();

    @NotNull @Unmodifiable Component toComponent();
    @NotNull @Unmodifiable Component toComponent(@NotNull Locale locale);
}
