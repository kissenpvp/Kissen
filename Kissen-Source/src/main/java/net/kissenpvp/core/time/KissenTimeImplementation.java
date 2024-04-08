package net.kissenpvp.core.time;

import net.kissenpvp.core.api.time.AccurateDuration;
import net.kissenpvp.core.api.time.TimeImplementation;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Period;
import java.time.format.DateTimeParseException;

public class KissenTimeImplementation implements TimeImplementation {

    @Override
    public @NotNull AccurateDuration parse(@NotNull String iso) throws DateTimeParseException {
        long time = 0;
        int current = 0;
        for (int i = 0; i < iso.toLowerCase().toCharArray().length; i++)
        {
            char currentChar = iso.toCharArray()[i];
            if(!String.valueOf(currentChar).matches("[0-9]+"))
            {
                long numbers = Integer.parseInt(iso.substring(current, i));
                time += switch (currentChar)
                {
                    case 'y' -> numbers * 1000 * 60 * 60 * 24 * 365;
                    case 'w' -> numbers * 1000 * 60 * 60 * 24 * 7;
                    case 'd' -> numbers * 1000 * 60 * 60 * 24;
                    case 'h' -> numbers * 1000 * 60 * 60;
                    case 'm' -> numbers * 1000 * 60;
                    case 's' -> numbers * 1000;
                    default -> 0;
                };
                current = i + 1;
            }
        }
        return new AccurateDuration(time);
    }

    @Override
    public @NotNull AccurateDuration millis(long milliseconds) {
        return new AccurateDuration(milliseconds);
    }

    @Override public @NotNull AccurateDuration seconds(long seconds)
    {
        return millis(seconds * 1000);
    }

    @Override public @NotNull AccurateDuration minutes(long minutes)
    {
        return seconds(minutes * 60);
    }

    @Override public @NotNull AccurateDuration hours(long hours)
    {
        return minutes(hours * 60);
    }

    @Override public @NotNull AccurateDuration days(long days)
    {
        return hours(days * 24);
    }

    @Override public @NotNull AccurateDuration weeks(long weeks)
    {
        return days(weeks * 7);
    }

    @Override public @NotNull AccurateDuration months(long months)
    {
        return weeks(months * 4);
    }

    @Override public @NotNull AccurateDuration years(long years)
    {
        return months(years * 12);
    }

    @Override
    public @NotNull AccurateDuration period(@NotNull Period period)
    {
        return new AccurateDuration(period);
    }

    @Override
    public @NotNull AccurateDuration duration(@NotNull Duration duration)
    {
        return new AccurateDuration(duration);
    }

}
