package net.kissenpvp.core.api.time;

import net.kissenpvp.core.api.base.Implementation;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Period;
import java.time.format.DateTimeParseException;

/**
 * An interface for implementations that accurately measure and convert time durations.
 * Parsing an ISO 8601 string representation of a duration and creating KissenTemporalMeasure instances
 * based on the duration in various time units are the main functionalities of this interface.
 * <p>
 * It extends the <code>Implementation</code> interface and provides methods to parse durations,
 * and to generate KissenTemporalMeasure objects from milliseconds, seconds, minutes, hours,
 * days, weeks, months, and years.
 * <p>
 * Each method that generates a KissenTemporalMeasure instance converts the provided time duration
 * into milliseconds to ensure accuracy and uniformity.
 * <p>
 * For simplicity, the conversion from months to milliseconds assumes each month has exactly 4 weeks,
 * and the conversion from years to milliseconds assumes each year has exactly 12 months.
 *
 */
public interface TimeImplementation extends Implementation {

    /**
     * Parses a string representation of a duration into an AccurateDuration object.
     *
     * @param iso the ISO 8601 string representation of a duration.
     * @return the parsed AccurateDuration object.
     * @throws DateTimeParseException if the string cannot be parsed into a valid AccurateDuration.
     */
    @NotNull AccurateDuration parse(@NotNull String iso) throws DateTimeParseException;

    /**
     * Creates a new instance of KissenTemporalMeasure based on the provided duration in milliseconds.
     *
     * @param milis the duration in milliseconds
     * @return a new instance of KissenTemporalMeasure representing the duration in milliseconds.
     */
    @NotNull AccurateDuration millis(long milis);

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

    @NotNull AccurateDuration period(@NotNull Period period);

    @NotNull AccurateDuration duration(@NotNull Duration duration);
}
