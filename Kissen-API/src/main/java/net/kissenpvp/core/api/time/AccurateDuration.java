package net.kissenpvp.core.api.time;

import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
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
public record AccurateDuration(long milliseconds)
{

    public AccurateDuration(@NotNull Duration duration)
    {
        this(duration.toMillis());
    }

    public AccurateDuration(@NotNull Period duration)
    {
        this(TimeUnit.DAYS.toMillis(duration.getDays()));
    }

    public @NotNull Duration getDuration()
    {
        return Duration.ofMillis(milliseconds);
    }

    public @NotNull Period getPeriod()
    {
        long days = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(milliseconds);
        return Period.ofDays((int) days);
    }

    public long getMillis()
    {
        return milliseconds();
    }

    public @NotNull @Unmodifiable Component toComponent(@NotNull ServerEntity entity)
    {
        return toComponent(entity.getCurrentLocale());
    }

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
                Component.text(months).appendSpace().append(Component.translatable(
                        "mco.configure.world.subscription.months")),
                Component.translatable("gui.days", Component.text(numberFormat.format(days))),
                Component.translatable("gui.hours", Component.text(numberFormat.format(hours))),
                Component.translatable("gui.minutes", Component.text(numberFormat.format(minutes)))
        };
        int nonZeroCount = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] != 0) {
                if (nonZeroCount > 0) {
                    if (nonZeroCount == 1 && i == data.length - 1) {
                        builder.append(Component.text(" and "));
                    } else if (nonZeroCount >= 1) {
                        builder.append(Component.text(", "));
                    }
                }
                builder.append(componentData[i]);
                nonZeroCount++;
            }
        }


        return builder.asComponent();
    }

}
