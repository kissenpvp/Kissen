package net.kissenpvp.api.punishment;

import org.jetbrains.annotations.NotNull;


/**
 * An enumeration representing the different types of punishments that can be applied.
 * The punishment types include ban, mute, and kick, each representing a specific
 * action or consequence.
 * <p>
 * This enumeration can be used to classify and manage the behavior associated with
 * each punishment type in systems that implement punishment mechanics.
 *
 * @author Ivo Quiring
 */
public enum PunishmentType
{
    BAN, MUTE, KICK;

    /**
     * Retrieves the {@link PunishmentType} corresponding to the specified ordinal value.
     * Ordinals represent the position of enumeration constants in their declaration order.
     *
     * @param ordinal The ordinal index of the {@link PunishmentType} to retrieve.
     *                This value must correspond to a valid enumeration constant's position.
     * @return The {@link PunishmentType} associated with the specified ordinal.
     * @throws ArrayIndexOutOfBoundsException If the provided ordinal is out of range for the {@link PunishmentType} enumeration.
     */
    public static @NotNull PunishmentType fromOrdinal(int ordinal) throws ArrayIndexOutOfBoundsException
    {
        return PunishmentType.values()[ordinal];
    }
}
