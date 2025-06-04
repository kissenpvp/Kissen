package net.kissenpvp.api.punishment;

import net.kissenpvp.api.database.PersistableEntity;
import net.kissenpvp.api.temporal.TemporalObject;
import net.kissenpvp.api.temporal.TemporalSubscriber;
import net.kissenpvp.api.temporal.timespan.TimeSpan;
import org.jetbrains.annotations.NotNull;

public interface Punishment extends PersistableEntity<Integer>
{
    @NotNull TimeSpan timeSpan();

    @NotNull PunishmentType punishmentType();
}
