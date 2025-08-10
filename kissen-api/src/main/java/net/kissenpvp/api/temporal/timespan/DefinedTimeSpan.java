package net.kissenpvp.api.temporal.timespan;

import java.time.temporal.TemporalAmount;


/**
 * Represents a well-defined, fixed span of time.
 * <p>
 * The {@code DefinedTimeSpan} interface extends both {@link TimeSpan} and {@link TemporalAmount},
 * allowing for precise temporal manipulation and query capabilities.
 *
 * @author Ivo Quiring
 * @see TimeSpan
 * @see TemporalAmount
 */
public interface DefinedTimeSpan extends TimeSpan, TemporalAmount
{}
