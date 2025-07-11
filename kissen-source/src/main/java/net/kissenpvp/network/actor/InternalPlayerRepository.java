package net.kissenpvp.network.actor;

import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.database.InternalRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.sql.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
        super("ksvp_player", connection, "SELECT linkId, username FROM %s WHERE id = ?;");
    }

    @Override
    protected @NotNull @UnmodifiableView PlayerClient toEntity(@NotNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(resultSet, "The result set cannot be null.");

        return this.toEntity(UUID.fromString(resultSet.getString("id")), resultSet);
    }

    @Override
    public @NotNull CompletableFuture<Void> saveAll(@NotNull Iterable<PlayerClient> id) throws NullPointerException
    {
        Objects.requireNonNull(id, "id cannot be null");

        String sql = """
                 INSERT INTO %s (id, linkId, username, first_login, last_login, operator, locale)\s
                 VALUES (?, ?, ?, ?, ?, ?, ?)\s
                 ON DUPLICATE KEY UPDATE linkId = ?, username = ?, last_login = ?, time_played = ?, operator = ?, locale = ?;\s
                 """;

        return CompletableFuture.supplyAsync(() -> query(sql, (statement ->
        {
            for (PlayerClient playerClient : id)
            {
                addBatch(statement, playerClient);
            }

            statement.executeBatch();
            return null;
        })));
    }

    /**
     * Adds a {@link PlayerClient} instance to the batch operation of the given {@link PreparedStatement}.
     * This method prepares and populates the SQL parameters for inserting or updating a player's data
     * and then calls {@link PreparedStatement#addBatch()} to include the operation in the batch.
     *
     * @param statement    the {@link PreparedStatement} to which the batch operation is added; must not be null
     * @param playerClient the {@link PlayerClient} instance whose data will populate the SQL parameters; must not be null
     * @throws SQLException         if an error occurs while interacting with the {@link PreparedStatement}
     * @throws NullPointerException if the provided {@link PreparedStatement} or {@link PlayerClient} is null
     */
    private void addBatch(@NotNull PreparedStatement statement, @NotNull PlayerClient playerClient) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(statement, "The prepared statement cannot be null.");
        Objects.requireNonNull(playerClient, "The player client cannot be null.");

        statement.setString(1, String.valueOf(playerClient.id()));

        Date now = Date.valueOf(Instant.now().atZone(ZoneId.systemDefault()).toLocalDate());
        Date lastLogin = Date.valueOf(playerClient.lastLogin().atZone(ZoneId.systemDefault()).toLocalDate());

        setDual(statement, 2, 8, Types.VARCHAR, String.valueOf(playerClient.linkId()));
        setDual(statement, 3, 9, Types.VARCHAR, playerClient.name());

        statement.setDate(4, now);
        setDual(statement, 5, 10, Types.DATE, lastLogin);
        statement.setLong(11, playerClient.timePlayed().get(ChronoUnit.SECONDS));

        setDual(statement, 6, 12, Types.BOOLEAN, playerClient.isOp());
        setDual(statement, 7, 13, Types.VARCHAR, playerClient.locale().toLanguageTag());

        overrideSignature(playerClient);
        statement.addBatch();
    }
}
