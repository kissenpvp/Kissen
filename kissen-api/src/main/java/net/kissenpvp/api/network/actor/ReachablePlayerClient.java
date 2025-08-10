package net.kissenpvp.api.network.actor;

/**
 * Represents a player client that is currently reachable and also capable
 * of receiving messages.
 * <p>
 * This is a specialized interface combining the functionalities
 * of {@link PlayerClient} and {@link MessageReceiver}.
 * <p>
 * A ReachablePlayerClient is identifiable by its stable unique identifier, provides
 * access to player-related metadata, and allows interaction through received messages.
 * It acts as both a participant in the networked environment and a message receiver,
 * typically using the {@link net.kyori.adventure.audience.Audience} API to process communications.
 * <p>
 * This interface serves as an abstraction for entities such as players who are both
 * network actors and capable of message handling and interactions.
 *
 * @author Ivo Quiring
 */
public interface ReachablePlayerClient extends PlayerClient, MessageReceiver {}
