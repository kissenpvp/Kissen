package net.kissenpvp.api.network.actor.rank;

import net.kissenpvp.api.database.PersistableEntity;
import net.kissenpvp.api.temporal.TemporalSubscriber;
import org.jspecify.annotations.NonNull;

/**
 * Represents a rank entity.
 * <p>
 * A rank is an identifiable entity with a specific priority level determining its precedence
 * over other ranks. Lower priority values indicate higher precedence within the rank's hierarchy.
 *
 * @author Ivo Quiring
 */
public interface Rank extends PersistableEntity<String>, TemporalSubscriber
{
    /**
     * Retrieves the priority of the rank.
     * The priority determines the ordering of ranks, where lower priority values indicate higher precedence.
     *
     * @return the priority value of the rank, determined as an integer
     */
    int priority();

    /**
     * Sets the priority of the rank.
     * The priority determines the ordering of ranks, where lower priority values indicate higher precedence.
     *
     * @param priority the priority value to be assigned to the rank; must be an integer
     */
    void priority(int priority);

    /**
     * Determines whether this rank has a higher priority than the given rank.
     * <p>
     * Note! This ranks priority must be greater than the others. If they are the same, it will return {@code false}
     *
     * @param other the rank to compare against
     * @return {@code true} if this rank has a higher priority than the given rank; {@code false} otherwise
     */
    boolean hasPriority(@NonNull Rank other);
}
