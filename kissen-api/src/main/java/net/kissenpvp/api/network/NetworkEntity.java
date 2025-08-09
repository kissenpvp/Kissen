package net.kissenpvp.api.network;

/**
 * Defines an entity that participates in the network, which may be a client or another kind of peer.
 * This interface acts as a base contract for components operating within a networked environment.
 * For example, a player can function as a client, as can a ConsoleSender in a Paper context.
 *
 * @author Ivo Quiring
 */
public interface NetworkEntity
{
    /**
     * Determines whether this network entity is a client.
     *
     * @return true if the network entity is a client, false otherwise.
     */
    boolean isClient();
}
