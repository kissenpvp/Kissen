package net.kissenpvp.api.network.actor;

import net.kissenpvp.api.database.PersistableEntity;
import net.kissenpvp.api.temporal.timespan.DefinedTimeSpan;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.UUID;

public interface PlayerClient extends AbstractActor, PersistableEntity<UUID>
{
    @NotNull UUID linkId();

    @NotNull Instant lastLogin();

    @NotNull DefinedTimeSpan timePlayed();


}
