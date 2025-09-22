package net.kissenpvp.temporal.timespan;

import net.kissenpvp.api.temporal.timespan.TimeSpan;

/**
 * Represents a permanent time span that does not have a defined duration or end point.
 * <p>
 * This class is typically used to model scenarios where an indefinite or infinite
 * time span is required. Unlike defined time spans, a {@code PermanentTimeSpan} cannot
 * be broken into measurable units or manipulated with operations like addition or subtraction.
 * <p>
 * It serves as a concrete implementation of the {@link TimeSpan} interface, specifically
 * representing a time span with unlimited duration.
 * <p>
 * Instances of this class are immutable and thread-safe.
 *
 * @author Ivo Quiring
 */
public class PermanentTimeSpan implements TimeSpan
{ }
