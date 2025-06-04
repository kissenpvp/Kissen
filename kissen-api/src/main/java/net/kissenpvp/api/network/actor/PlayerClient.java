package net.kissenpvp.api.network.actor;

import net.kissenpvp.api.database.PersistableEntity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface PlayerClient extends AbstractActor, PersistableEntity<UUID>
{
    @NotNull UUID linkId();
}
