package net.kissenpvp.network.actor.operator;

import net.kissenpvp.api.database.PersistableEntity;

import java.util.UUID;

public interface OperatorInfo extends PersistableEntity<UUID>
{
    int getLevel();

    boolean getBypassesPlayerLimit();
}
