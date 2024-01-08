package net.kissenpvp.core.time;

import net.kissenpvp.core.api.message.localization.LocalizationImplementation;
import net.kissenpvp.core.api.time.AccurateDuration;
import net.kissenpvp.core.base.KissenCore;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.text.NumberFormat;
import java.time.Duration;
import java.time.Period;
import java.util.Locale;

/**
 * Record representing a measure of time in milliseconds.
 * <p>
 * This is used to conveniently convert time from various units to milliseconds.
 */
public record KissenAccurateDuration(long milliseconds) implements AccurateDuration {

    public @NotNull Duration getDuration() {
        return Duration.ofMillis(milliseconds);
    }

    public @NotNull Period getPeriod() {
        long days = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(milliseconds);
        return Period.ofDays((int) days);
    }

    public long getMillis() {
        return milliseconds();
    }

    @Override
    public @NotNull @Unmodifiable Component toComponent()
    {
        return toComponent(KissenCore.getInstance().getImplementation(LocalizationImplementation.class).getDefaultLocale());
    }

    @Override
    public @NotNull @Unmodifiable Component toComponent(@NotNull Locale locale)
    {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long years = days / 365;

        seconds %= 60;
        minutes %= 60;
        hours %= 24;
        days %= 365;

        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        Component[] args = {
                Component.text(numberFormat.format(years)),
                Component.text(numberFormat.format(days)),
                Component.text(numberFormat.format(hours)),
                Component.text(numberFormat.format(minutes)),
                Component.text(numberFormat.format(seconds)),
        };

        return Component.translatable("server.general.duration", args);
    }
}
