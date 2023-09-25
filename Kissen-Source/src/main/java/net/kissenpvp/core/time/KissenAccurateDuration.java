package net.kissenpvp.core.time;

import net.kissenpvp.core.api.time.AccurateDuration;
import net.kissenpvp.core.api.time.TimeUnit;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Record representing a measure of time in milliseconds.
 * <p>
 * This is used to conveniently convert time from various units to milliseconds.
 */
public record KissenAccurateDuration(long milliseconds) implements AccurateDuration {

    public KissenAccurateDuration(@NotNull String iso)
    {
        this(transformISOFormat(iso.toUpperCase()));
    }

    /**
     * Transforms an input string in ISO format to a long value representing the total time.
     *
     * @param input the input string in ISO format
     * @return the total time in milliseconds
     */
    private static long transformISOFormat(String input) {
        return calculateTotalTime(distinguish(identifyTemporalMeasures(input)));
    }

    /**
     * Calculates the total time based on the provided data.
     *
     * @param data the data used to calculate the total time. It should be a 2-dimensional array
     *             where the first element represents the period and the second element represents the duration.
     *             Each element should be a string containing the relevant temporal measures.
     *             The first element should follow the ISO-8601 period format (e.g., "P1Y2M3W4DT5H6M7S").
     *             The second element should follow the ISO-8601 duration format (e.g., "PT5H30M").
     *
     * @return the total time in milliseconds.
     */
    private static long calculateTotalTime(String[][] data) {
        long totalTime = 0;

        String period = String.join("", data[0]);
        if (!period.isBlank()) {
            totalTime += periodMillis(Period.parse("P" + period));
        }

        String duration = String.join("", data[1]);
        if (!duration.isBlank()) {
            totalTime += Duration.parse("PT" + duration).toMillis();
        }

        return totalTime;
    }


    /**
     * Calculates the number of milliseconds in the specified period.
     *
     * @param period the string representation of the period in ISO-8601 format (e.g., "P1Y2M3W4DT5H6M7S").
     * @return the number of milliseconds in the specified period.
     */
    private static long periodMillis(@NotNull Period period) {
        ZonedDateTime now = ZonedDateTime.now(), then = now.plus(period);
        return then.toInstant().toEpochMilli() - now.toInstant().toEpochMilli();
    }

    /**
     * Identifies temporal measures from the given input string.
     *
     * @param input the input string to identify temporal measures from
     * @return an array of identified temporal measures
     */
    private static @NotNull String[] identifyTemporalMeasures(String input) {
        List<String> temporalMeasureList = new ArrayList<>();
        for (int i = 0; i < input.length() - 1; i++) {
            if (input.substring(i, i + 2).matches("[0-9][YWMDHS]")) {
                temporalMeasureList.add(input.substring(i, i + 2));
            }
        }
        return temporalMeasureList.toArray(new String[0]);
    }

    /**
     * Distinguishes elements of the given data array based on their time character.
     *
     * @param data an array of strings containing time elements
     * @return a two-dimensional array where the first sub-array consists of elements that have a time character of type 'P'
     *         and the second sub-array consists of elements that have a time character other than 'P'
     */
    private static @NotNull String[][] distinguish(String[] data) {
        List<String> periodData = new ArrayList<>(), durationData = new ArrayList<>();
        for (String element : data) {
            if (TimeUnit.PERIOD.appliesTo(element.charAt(1))) {
                periodData.add(element);
                continue;
            }
            durationData.add(element);
        }
        return new String[][]{periodData.toArray(new String[0]), durationData.toArray(new String[0])};
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
}
