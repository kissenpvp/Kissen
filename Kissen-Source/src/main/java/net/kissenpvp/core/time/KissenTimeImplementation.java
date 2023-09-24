package net.kissenpvp.core.time;

import net.kissenpvp.core.api.time.AccurateDuration;
import net.kissenpvp.core.api.time.TimeImplementation;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeParseException;

public class KissenTimeImplementation implements TimeImplementation {

    @Override
    public @NotNull AccurateDuration parse(@NotNull String iso) throws DateTimeParseException {
        return new KissenAccurateDuration(iso);
    }

    @Override
    public AccurateDuration millis(@NotNull long milliseconds) {
        return new KissenAccurateDuration(milliseconds);
    }

    @Override public @NotNull AccurateDuration  seconds(@NotNull long seconds)
    {
        return millis(seconds * 1000);
    }

    @Override public @NotNull AccurateDuration   minutes(@NotNull long minutes)
    {
        return seconds(minutes * 60);
    }

    @Override public @NotNull AccurateDuration   hours(@NotNull long hours)
    {
        return minutes(hours * 60);
    }

    @Override public @NotNull AccurateDuration   days(@NotNull long days)
    {
        return hours(days * 24);
    }

    @Override public @NotNull AccurateDuration   weeks(@NotNull long weeks)
    {
        return days(weeks * 7);
    }

    @Override public @NotNull AccurateDuration   months(@NotNull long months)
    {
        return weeks(months * 4);
    }

    @Override public @NotNull AccurateDuration  years(@NotNull long years)
    {
        return months(years * 12);
    }

}