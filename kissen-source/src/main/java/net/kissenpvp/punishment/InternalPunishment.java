package net.kissenpvp.punishment;

import net.kissenpvp.api.punishment.Punishment;
import net.kissenpvp.api.punishment.PunishmentType;
import net.kissenpvp.api.temporal.timespan.TimeSpan;
import org.jetbrains.annotations.NotNull;

public class InternalPunishment implements Punishment
{
    private final Integer id;
    private final PunishmentType punishmentType;
    private final TimeSpan timeSpan;

    public InternalPunishment(Integer id, PunishmentType punishmentType, TimeSpan timeSpan)
    {
        this.id = id;
        this.punishmentType = punishmentType;
        this.timeSpan = timeSpan;
    }

    @Override public @NotNull TimeSpan timeSpan()
    {
        return timeSpan;
    }

    @Override public @NotNull PunishmentType punishmentType()
    {
        return punishmentType;
    }

    @Override public @NotNull Integer id()
    {
        return id;
    }
}
