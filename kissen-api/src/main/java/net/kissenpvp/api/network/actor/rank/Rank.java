package net.kissenpvp.api.network.actor.rank;

import net.kissenpvp.api.database.PersistableEntity;

/**
 *
 */
public interface Rank extends PersistableEntity<String>
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
