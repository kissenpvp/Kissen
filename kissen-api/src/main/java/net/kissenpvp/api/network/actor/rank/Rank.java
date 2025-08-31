package net.kissenpvp.api.network.actor.rank;

import net.kissenpvp.api.database.PersistableEntity;
import net.kissenpvp.api.temporal.TemporalSubscriber;

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
}
