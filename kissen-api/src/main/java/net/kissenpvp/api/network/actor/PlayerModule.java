package net.kissenpvp.api.network.actor;

import net.kissenpvp.api.database.Repository;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Defines the base module for managing player-related operations.
 *
 * @author Ivo Quiring
 */
public interface PlayerModule
{

    /**
     * Provides access to the {@link PlayerRepository}, enabling CRUD operations
     * for managing {@link PlayerClient} entities in the underlying database.
     *
     * @return a non-null instance of {@link PlayerRepository}, specialized for handling
     * {@link PlayerClient} entities
     */
    @NotNull PlayerRepository playerRepository();

    @NotNull Repository<UUID, OperatorInfo> operatorRepository();
}
