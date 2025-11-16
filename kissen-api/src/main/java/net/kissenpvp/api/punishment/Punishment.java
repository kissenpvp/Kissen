package net.kissenpvp.api.punishment;

import net.kissenpvp.api.database.PersistableEntity;
import net.kissenpvp.api.temporal.timespan.TimeSpan;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Defines a punishment descriptor that captures the details of a potential consequence,
 * such as a ban, mute, or kick.
 * <p>
 * It offers methods to manage the punishment's type, duration, and default message.
 * This describes a selectable option for the support team rather than an already applied punishment.
 * <p>
 * Extends {@link PersistableEntity}, making sure each implementation has a unique identifier
 * and supports persistence-related operations.
 *
 * @author Ivo Quiring
 */
public interface Punishment extends PersistableEntity<Integer>
{
    /**
     * Retrieves the {@link TimeSpan} associated with this {@link Punishment}.
     * The {@link TimeSpan} represents the duration or temporal span during which
     * the punishment is active or applicable.
     *
     * @return a non-null {@link TimeSpan} instance representing the duration or span of the punishment.
     */
    @NonNull TimeSpan timeSpan();

    /**
     * Sets the {@link TimeSpan} for this {@link Punishment}.
     * The {@link TimeSpan} defines the duration or temporal span during which
     * the punishment is active or applicable.
     *
     * @param timeSpan the {@link TimeSpan} to be associated with this punishment; must not be null
     * @throws NullPointerException if the provided {@link TimeSpan} is null
     */
    void timeSpan(@NonNull TimeSpan timeSpan);

    /**
     * Retrieves the type of the punishment represented by this instance.
     * The punishment type determines the specific consequence, such as a ban, mute, or kick,
     * and is represented by the {@link PunishmentType} enumeration.
     *
     * @return a non-null {@link PunishmentType} indicating the kind of punishment.
     */
    @NonNull PunishmentType punishmentType();

    /**
     * Sets the type of punishment for this {@link Punishment}.
     * The type defines the specific action to be applied, such as {@link PunishmentType#BAN},
     * {@link PunishmentType#MUTE}, or {@link PunishmentType#KICK}.
     *
     * @param punishmentType the {@link PunishmentType} to be associated with this punishment; must not be null
     * @throws NullPointerException if the provided {@link PunishmentType} is null
     */
    void punishmentType(@NonNull PunishmentType punishmentType);

    /**
     * Retrieves the default message associated with this {@link Punishment}.
     * The default message represents a general description or note that can be used,
     * for example, during the application of the punishment or for logging purposes.
     *
     * @return an {@link Optional} containing the default {@link Component} message if present, or an empty
     * {@link Optional} if no default message is set.
     */
    @NonNull Optional<Component> defaultMessage();

    /**
     * Sets the default message to be associated with this {@link Punishment}.
     * The default message represents a general description or note that can be used,
     * for example, during the application of the punishment or for logging purposes.
     *
     * @param defaultMessage the {@link Component} representing the default message; can be null to unset the current
     *                       default message.
     * @see #defaultMessage()
     */
    void defaultMessage(@Nullable Component defaultMessage);


    /**
     * Unsets the current default message associated with this {@link Punishment}.
     * This operation removes any previously set message, effectively leaving
     * the punishment without a default message.
     *
     * @see #defaultMessage(Component)
     */
    void unsetMessage();
}
