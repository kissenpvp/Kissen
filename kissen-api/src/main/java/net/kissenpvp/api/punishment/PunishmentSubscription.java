package net.kissenpvp.api.punishment;

import net.kissenpvp.api.database.SubscriptionEntity;
import net.kissenpvp.api.network.actor.Actor;
import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.api.temporal.WritableTemporalSubscriber;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a subscription instance that applies a specific {@link Punishment} to a linked identity.
 * <p>
 * The association to the affected user/account is expressed via {@link #linkId()}, which enables
 * correlating multiple accounts of the same person for cross-account enforcement.
 *
 * @author Ivo Quiring
 * @see net.kissenpvp.api.network.actor.PlayerClient#linkId() 
 */
public interface PunishmentSubscription extends SubscriptionEntity<String, Integer, Punishment>, WritableTemporalSubscriber {

    /**
     * Returns the stable link identifier that associates this subscription with the affected user or linked account group.
     * <p>
     * This value is used to correlate multiple accounts belonging to the same person for cross-account enforcement.
     *
     * @return a non-null {@link UUID} identifying the linked identity for this subscription
     * @see net.kissenpvp.api.network.actor.PlayerClient#linkId()
     */
    @NotNull UUID linkId();

    @NotNull @UnmodifiableView Collection<PlayerClient> target();

    @NotNull Actor operator();

    /**
     * Retrieves the custom message associated with this subscription, if present.
     * <p>
     * When set, this message can be displayed to the affected players.
     *
     * @return a non-null {@link Optional} containing the {@link Component} message if one is set; otherwise an empty {@link Optional}
     */
    @NotNull Optional<Component> message();

    /**
     * Sets or clears the custom message associated with this subscription.
     * <p>
     * Providing a non-null value sets or replaces the current message. Providing {@code null} removes any existing message,
     * equivalent to calling {@link #unsetMessage()}.
     *
     * @param component the message to associate with this subscription, or {@code null} to clear it
     */
    void message(@Nullable Component component);

    /**
     * Unsets (clears) the custom message associated with this subscription.
     * <p>
     * After calling this method, {@link #message()} will return an empty {@link Optional}.
     * @see #message(Component) 
     */
    void unsetMessage();
}
