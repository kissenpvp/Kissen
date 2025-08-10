package net.kissenpvp.api.network.actor;

/**
 * Describes a console-backed client in the network.
 * <p>
 * A ConsoleClient combines the roles of
 * {@link AbstractActor} and {@link MessageReceiver}, acting as a network actor while also being
 * capable of receiving and processing messages.
 * <p>
 * As an {@link AbstractActor}, it provides an identifying name (commonly "CONSOLE") and a locale
 * indicating language and regional preferences.
 * <p>
 * In most cases, a ConsoleClient has elevated permissions (for example, {@link #isOp()} typically
 * returns {@code true}).
 * <p>
 * As a {@link MessageReceiver}, it can handle communication via the {@link net.kyori.adventure.audience.Audience} API.
 *
 * @author Ivo Quiring
 */
public interface ConsoleClient extends AbstractActor, MessageReceiver
{}
