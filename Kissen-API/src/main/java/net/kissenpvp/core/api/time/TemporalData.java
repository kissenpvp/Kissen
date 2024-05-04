package net.kissenpvp.core.api.time;

import net.kissenpvp.core.api.util.Container;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;


/**
 * This is a record class named {@code TemporalNode} used for storing and handling temporal data.
 * It contains different constructors to handle various types of input data.
 * <p>
 * The {@code TemporalNode} record encapsulates five different attributes:
 * <ul>
 *  <li>{@code start}: a {@code long} value representing the start of a temporal period.</li>
 *  <li>{@code duration}: a {@code Container} encapsulating a {@code Long} value that represents the duration of
 *   a expire period. It's declared as {@code @NotNull}, therefore it cannot take a {@code null} value.</li>
 *  <li>{@code end}: a {@code Container} encapsulating a {@code Long} value representing the end of a expire period.
 *   It's declared as {@code @NotNull}, hence it can never be {@code null}.</li>
 *  <li>{@code predictedEnd}: a {@code Long} value that represents the forecasted end of the expire period.
 *   This value is declared as {@code @Nullable} and thus can also be {@code null}.</li>
 * </ul>
 *
 * This record has several constructors to offer flexibility in creating instances depending on available
 * data and usage contexts.
 */
public record TemporalData(long start, @NotNull Container<Long> duration, @NotNull Container<Long> end, @Nullable Long predictedEnd) {

    /**
     * This constructor creates an instance of {@code TemporalNode} using an {@code TemporalObject}.
     * It retrieves the required information from the {@code TemporalObject} and maps the types appropriately.
     *
     * <p> In particular, it maps the following:
     * <ul>
     *  <li>The start expire is retrieved from {@code TemporalObject} using the {@code getStart()} method and converted to milliseconds since epoch using the {@code toEpochMilli()} method.</li>
     *  <li>The duration is retrieved from {@code TemporalObject} using the {@code getDuration()} method. It is provided as an {@code Optional} which may contain a {@code PeriodDuration}. The duration in milliseconds is extracted using the {@code getMillis()} method if it exists, otherwise it is set to {@code null}.</li>
     *  <li>The end expire is similarly retrieved from {@code TemporalObject} with the {@code getEnd()} method, returning an {@code Optional} that may contain an {@code Instant}. This is converted to milliseconds since epoch with the {@code toEpochMilli()} method if it exists, otherwise it is set to {@code null}.</li>
     * </ul>
     *
     * @param temporalObject the {@code TemporalObject} from which to create the {@code TemporalNode}.
     * @throws NullPointerException if {@code temporalObject} is {@code null}.
     * @implNote {@code temporalObject} must be non-null; if {@code temporalObject} is {@code null}, a {@code NullPointerException} will be thrown.
     */
    public TemporalData(@NotNull TemporalObject temporalObject) {
        this(temporalObject.getStart().toEpochMilli(), temporalObject.getAccurateDuration().map(AccurateDuration::getMillis).orElse(null), temporalObject.getEnd().map(Instant::toEpochMilli).orElse(null));
    }

    /**
     * This constructor provides input flexibility by accepting four arguments that corresponds to the
     * attributes in the {@code TemporalNode} record.
     *
     * @param start a {@code long} value representing the start of a period.
     * @param duration a {@code Nullable Long} value representing the duration of a period.
     * If it's {@code null}, it will be replaced by a new {@code Container} object with its value as {@code null}.
     * @param end is a {@code Nullable Long} that represents the end of the expire period.
     * If it's {@code null}, it will be replaced by a new {@code Container} object with its value as {@code null}.
     * @param predictedEnd a {@code Nullable Long} value that represents the predicted end of the expire period.
     * It is allowed to be {@code null}.
     */
    public TemporalData(long start, @Nullable Long duration, @Nullable Long end, @Nullable Long predictedEnd) {
        this(start, new Container<>(duration), new Container<>(end), predictedEnd);
    }

    /**
     * This constructor takes in three arguments and assigns the {@code end} value to the {@code predictedEnd} attribute.
     *
     * @param start a {@code long} value representing the start of the expire period.
     * @param duration a {@code Nullable Long} value that represents the duration of the expire period.
     * @param end a {@code Nullable Long} value that represents the end of the expire period.
     */
    public TemporalData(long start, @Nullable Long duration, @Nullable Long end) {
        this(start, duration, end, end);
    }

    /**
     * This constructor allows for creating instances of {@code TemporalNode} with a {@code PeriodDuration} object.
     *
     * @param start a {@code long} value representing the start of a period.
     * @param accurateDuration a {@code Nullable PeriodDuration} object which represents the duration of the period.
     * Its {@code null} value will be replaced by a {@code null} value for the duration.
     * @param end a {@code Nullable Long} value that represents the end of the expire period.
     */
    public TemporalData(long start, @Nullable AccurateDuration accurateDuration, @Nullable Long end) {
        this(start, accurateDuration != null ? accurateDuration.getMillis() : null, end);
    }

    /**
     * This constructor offers flexibility in creating instances when only the start point and duration is available.
     * The end point is then calculated based on the start point and the duration.
     *
     * @param start a {@code long} value representing the start of the expire period.
     * @param accurateDuration a {@code Nullable PeriodDuration} object which represents the duration of the period.
     * Its {@code null} value will be calculated to be the same as the start point.
     */
    public TemporalData(long start, @Nullable AccurateDuration accurateDuration) {
        this(start, accurateDuration, accurateDuration != null ? start + accurateDuration.getMillis() : null);
    }

    /**
     * This constructor creates a new instance of {@code TemporalNode}, using the current system expire as the start expire,
     * and a {@code Nullable PeriodDuration} object as the duration.
     *
     * <p> Here's how it is handled:
     * <ul>
     *  <li>If the {@code periodDuration} is not {@code null}, then the end expire point is calculated as the start expire point plus the period duration in milliseconds.</li>
     *  <li>If the {@code periodDuration} is {@code null}, then the end expire point is considered as not specified and is assigned a {@code null} value.</li>
     * </ul>
     *
     * @param accurateDuration a {@code Nullable PeriodDuration} object which represents the duration of the period.
     * @implNote The start expire is set to the current system expire in milliseconds since epoch at the moment of object
     * instantiation, retrieved using {@code System.currentTimeMillis()}.
     */
    public TemporalData(@Nullable AccurateDuration accurateDuration) {
        this(System.currentTimeMillis(), accurateDuration);
    }


    public TemporalData(@NotNull long start) {
        this(start, null);
    }

    public TemporalData() {
        this(System.currentTimeMillis(), null);
    }
}
