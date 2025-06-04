package net.kissenpvp.api.punishment;

import org.jetbrains.annotations.Nullable;

public enum PunishmentType
{
    BAN,
    MUTE,
    KICK;

    public static @Nullable PunishmentType fromOrdinal(int ordinal)
    {
        return PunishmentType.values()[ordinal];
    }
}
