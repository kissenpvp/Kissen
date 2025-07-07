package net.kissenpvp.network.actor;

import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.database.InternalRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Represents an abstract repository responsible for managing and persisting instances of
 * {@link PlayerClient} identified by {@link UUID}. This repository implementation interacts
 * with a relational database using SQL queries to handle data operations.
 * <p>
 * The repository specifically targets the `ksvp_player_data` table for storing player-related
 * information, and provides mechanisms for mapping {@link ResultSet} data to domain entities,
 * and for persisting {@link PlayerClient} objects as database rows.
 * <p>
 * The concrete implementation is expected to extend this class and utilize the provided
 * functionality to manage player data.
 * <p>
 * Constructor Summary:
 * - Ensures a non-null {@link Connection} is provided to interact with the database.
 * <p>
 * Key Functionalities:
 * - Converts {@link ResultSet} data into {@link PlayerClient} entities.
 * - Batch-saving a collection of {@link PlayerClient} objects into the database with support
 * for updating existing records based on their unique identifiers.
 *
 * @author Ivo Quiring
 */
public abstract class InternalPlayerRepository extends InternalRepository<UUID, PlayerClient>
{
    /**
     * Constructs an instance of {@code InternalPlayerRepository}, providing a mechanism for
     * managing and persisting {@link PlayerClient} instances in the `ksvp_player_data` table.
     * The repository uses SQL queries to perform data operations on the associated database.
     *
     * @param connection A non-null {@link Connection} to the database. This connection must be
     *                   valid and active to facilitate SQL operations. If {@code connection} is null,
     *                   a {@link NullPointerException} is thrown.
     * @throws NullPointerException Thrown if the provided {@code connection} is null.
     */
    public InternalPlayerRepository(@NotNull Connection connection) throws NullPointerException
    {
        super("ksvp_player_data", connection, "SELECT linkId, username FROM %s WHERE id = ?;");
    }

    @Override
    protected @NotNull @UnmodifiableView PlayerClient toEntity(@NotNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        return this.toEntity(UUID.fromString(resultSet.getString("id")), resultSet);
    }

    @Override
    public @NotNull CompletableFuture<Void> saveAll(@NotNull Iterable<PlayerClient> id) throws NullPointerException
    {
        Objects.requireNonNull(id, "id cannot be null");

        String sql = "INSERT INTO %s (id, linkId, username) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE linkId = ?, username = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, (statement ->
        {
            for (PlayerClient playerClient : id)
            {
                statement.setString(1, String.valueOf(playerClient.id()));

                statement.setString(2, String.valueOf(playerClient.linkId()));
                statement.setString(4, String.valueOf(playerClient.linkId()));

                statement.setString(3, playerClient.name());
                statement.setString(5, playerClient.name());

                overrideSignature(playerClient);
                statement.addBatch();
            }

            statement.executeBatch();
            return null;
        })));
    }
}
