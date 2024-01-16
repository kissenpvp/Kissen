package net.kissenpvp.core.time;

import net.kissenpvp.core.api.message.ThemeProvider;
import net.kissenpvp.core.api.message.localization.LocalizationImplementation;
import net.kissenpvp.core.api.time.AccurateDuration;
import net.kissenpvp.core.base.KissenCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.text.NumberFormat;
import java.time.Duration;
import java.time.Period;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Record representing a measure of expire in milliseconds.
 * <p>
 * This is used to conveniently convert expire from various units to milliseconds.
 */
public record KissenAccurateDuration(long milliseconds) implements AccurateDuration {

    public KissenAccurateDuration(@NotNull Duration duration)
    {
        this(duration.toMillis());
    }

    public KissenAccurateDuration(@NotNull Period duration)
    {
        this(TimeUnit.DAYS.toMillis(duration.getDays()));
    }

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
        long minutes = (milliseconds / 1000) / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long months = days / 31;

        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        TextComponent.Builder builder = Component.text();

        long[] data = {months, days %= 365, hours %= 24, minutes %= 60};
        Component[] componentData = {
                Component.text(months, ThemeProvider.primary()).appendSpace().append(Component.translatable("mco.configure.world.subscription.months")),
                Component.translatable("gui.days", Component.text(numberFormat.format(days))),
                Component.translatable("gui.hours", Component.text(numberFormat.format(hours))),
                Component.translatable("gui.minutes", Component.text(numberFormat.format(minutes)))
        };
        for (int i = 0; i < data.length; i++)
        {
            if (data[i] != 0)
            {
                builder.append(componentData[i]);
                if (i < data.length - 2)
                {
                    builder.append(Component.text(", "));
                }
                else if (i < data.length - 1)
                {
                    builder.append(Component.text(" and "));
                }
            }
        }

        return builder.asComponent();
    }

}
