package net.kissenpvp.api.network.actor;

import org.jetbrains.annotations.NotNull;

public interface PlayerModule
{

    /**
     * Provides access to the {@link PlayerRepository}, enabling CRUD operations
     * for managing {@link PlayerClient} entities in the underlying database.
     *
     * @return a non-null instance of {@link PlayerRepository}, specialized for handling
     *         {@link PlayerClient} entities
     */
    @NotNull PlayerRepository playerRepository();
}
