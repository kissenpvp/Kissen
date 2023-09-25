package net.kissenpvp.core.api.time;

import net.kissenpvp.core.api.base.Implementation;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeParseException;

public interface TimeImplementation extends Implementation {

    @NotNull AccurateDuration parse(@NotNull String iso) throws DateTimeParseException;

    /**
     * Creates a new instance of KissenTemporalMeasure based on the provided duration in milliseconds.
     *
     * @param milis the duration in milliseconds
     * @return a new instance of KissenTemporalMeasure representing the duration in milliseconds.
     */
    AccurateDuration millis(long milis);

    /**
     * Creates a new instance of KissenTemporalMeasure based on the provided duration in seconds.
     * The provided seconds are first converted to milliseconds.
     *
     * @param seconds the duration in seconds.
     * @return a new instance of KissenTemporalMeasure representing the duration in milliseconds.
     */
    @NotNull AccurateDuration seconds(long seconds);

    /**
     * Creates a new instance of KissenTemporalMeasure based on the provided duration in minutes.
     * The provided minutes are first converted to seconds and then to milliseconds.
     *
     * @param minutes the duration in minutes.
     * @return a new instance of KissenTemporalMeasure representing the duration in milliseconds.
     */
    @NotNull AccurateDuration minutes(long minutes);
    /**
     * Creates a new instance of KissenTemporalMeasure based on the provided duration in hours.
     * The provided hours are first converted to minutes, then to seconds, and finally to milliseconds.
     *
     * @param hours the duration in hours.
     * @return a new instance of KissenTemporalMeasure representing the duration in milliseconds.
     */
    @NotNull AccurateDuration hours(long hours);

    /**
     * Creates a new instance of KissenTemporalMeasure based on the provided duration in days.
     * The provided days are first converted to hours, then to minutes, then to seconds, and finally to milliseconds.
     *
     * @param days the duration in days.
     * @return a new instance of KissenTemporalMeasure representing the duration in milliseconds.
     */
    @NotNull AccurateDuration days(long days);

    /**
     * Creates a new instance of KissenTemporalMeasure based on the provided duration in weeks.
     * The provided weeks are first converted to days, then to hours, then to minutes, then to seconds, and finally to milliseconds.
     *
     * @param weeks the duration in weeks.
     * @return a new instance of KissenTemporalMeasure representing the duration in milliseconds.
     */
    @NotNull AccurateDuration weeks(long weeks);

    /**
     * Creates a new instance of KissenTemporalMeasure based on the provided duration in months.
     * For simplicity, it is assumed that each month contains exactly 4 weeks.
     * The provided months are first converted to weeks, then to days, then to hours, then to minutes, then to seconds, and finally to milliseconds.
     *
     * @param months the duration in months.
     * @return a new instance of KissenTemporalMeasure representing the duration in milliseconds.
     */
    @NotNull AccurateDuration months(long months);

    /**
     * Creates a new instance of KissenTemporalMeasure based on the provided duration in years.
     * For simplicity, it is assumed that each year contains exactly 12 months.
     * The provided years are first converted to months, then to weeks, then to days, then to hours, then to minutes, then to seconds, and finally to milliseconds.
     *
     * @param years the duration in years.
     * @return a new instance of KissenTemporalMeasure representing the duration in milliseconds.
     */
    @NotNull AccurateDuration years(long years);



}
