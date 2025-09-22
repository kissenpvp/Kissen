package net.kissenpvp.network.actor;

import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.api.network.actor.PlayerRepository;
import net.kissenpvp.database.InternalCachedRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.sql.*;
import java.sql.Date;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Stream;

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
public abstract class InternalPlayerRepository extends InternalCachedRepository<UUID, PlayerClient> implements PlayerRepository
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
        super(connection);

    }

    @Override protected @NotNull CompletableFuture<Optional<PlayerClient>> findUncached(@NotNull UUID id) throws NullPointerException
    {
        String sql = "SELECT link_id, username, first_login, last_login, time_played, locale FROM ksvp_player WHERE id = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, (statement ->
        {
            statement.setString(1, String.valueOf(id));
            return collectResults(id, statement).stream().findFirst();
        })));
    }

    @Override protected @NotNull CompletableFuture<@UnmodifiableView Collection<PlayerClient>> findAllUncached(@NotNull Iterable<UUID> id) throws NullPointerException
    {
        String placeHolders = String.join(", ", Collections.nCopies(computeIterableSize(id), "?"));
        String sql = "SELECT id, link_id, username, first_login, last_login, time_played, locale FROM ksvp_player WHERE id IN (" + placeHolders + ");";
        return CompletableFuture.supplyAsync(() -> query(sql, statement ->
        {
            int index = 1;
            for (UUID current : id)
            {
                statement.setString(index++, String.valueOf(current));
            }
            return collectResults(statement);
        }));
    }

    @Override public @NotNull CompletableFuture<Boolean> has(@NotNull UUID id) throws NullPointerException
    {
        String sql = "SELECT id FROM ksvp_player WHERE id = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, statement ->
        {
            statement.setString(1, String.valueOf(id));
            return hasResult(statement);
        }));
    }

    @Override public @NotNull CompletableFuture<@UnmodifiableView Collection<PlayerClient>> findAll()
    {
        String sql = "SELECT id, link_id, username, first_login, last_login, time_played, locale FROM ksvp_player;";
        return CompletableFuture.supplyAsync(() -> query(sql, this::collectResults));
    }

    @Override public @NotNull CompletableFuture<@NotNull Optional<PlayerClient>> findByName(@NotNull String name) throws NullPointerException
    {
        return findByName(name, true);
    }

    @Override public @NotNull CompletableFuture<@NotNull Optional<UUID>> findLinkId(@NotNull UUID uuid) throws NullPointerException
    {
        String sql = "SELECT link_id FROM ksvp_player WHERE id = ?;";
        return CompletableFuture.supplyAsync(() -> Objects.requireNonNull(query(sql, (statement ->
        {
            statement.setString(1, String.valueOf(uuid));
            try (ResultSet resultSet = statement.executeQuery())
            {
                if (!resultSet.next())
                {
                    return Optional.empty();
                }

                return Optional.of(UUID.fromString(resultSet.getString("link_id")));
            }
        }))));
    }

    @Override public @NotNull CompletableFuture<@NotNull Optional<PlayerClient>> findByName(@NotNull String name, boolean utilizeCache) throws NullPointerException
    {
        if(utilizeCache)
        {
            for(PlayerClient playerClient : cachedEntries().values())
            {
                if(!Objects.equals(name, playerClient.username()))
                {
                    continue;
                }

                return CompletableFuture.completedFuture(Optional.of(playerClient));
            }
        }

        String sql = "SELECT id, link_id, username, first_login, last_login, time_played, locale  FROM ksvp_player WHERE username = ?;";
        return CompletableFuture.supplyAsync(() -> Objects.requireNonNull(query(sql, (statement ->
        {
            statement.setString(1, name);
            return collectResults(statement).stream().findFirst();
        }))));
    }


    @Override public @NotNull CompletableFuture<@UnmodifiableView Collection<PlayerClient>> findAllByName(@NotNull Iterable<String> name, boolean utilizeCache) throws NullPointerException
    {
        if (!utilizeCache)
        {
            return findAllByNameUncached(name);
        }

        Collection<PlayerClient> cached = new ArrayList<>();
        Collection<String> uncached = new ArrayList<>();

        Collection<PlayerClient> cachedEntries = cachedEntries().values();
        for (String currentId : name)
        {
            Stream<PlayerClient> playerClient = cachedEntries.stream();
            Predicate<PlayerClient> nameEquals = player -> Objects.equals(player.username(), currentId);
            playerClient.filter(nameEquals).findFirst().ifPresentOrElse((cached::add), () -> uncached.add(currentId));
        }

        if (!uncached.isEmpty())
        {
            return findAllByNameUncached(uncached).thenApply(list ->
            {
                Stream<PlayerClient> currentlyCached = cached.stream();
                return Stream.concat(list.stream(), currentlyCached).toList();
            });
        }
        return CompletableFuture.completedFuture(Collections.unmodifiableCollection(cached));
    }

    private @NotNull CompletableFuture<@UnmodifiableView Collection<PlayerClient>> findAllByNameUncached(@NotNull Iterable<String> name)
    {
        String placeHolders = String.join(", ", Collections.nCopies(computeIterableSize(name), "?"));
        String sql = "SELECT id, link_id, username, first_login, last_login, time_played, locale FROM ksvp_player WHERE username IN (" + placeHolders + ");";
        return CompletableFuture.supplyAsync(() -> query(sql, statement ->
        {
            int index = 1;
            for(String current : name)
            {
                statement.setString(index++, current);
            }

            return collectResults(statement);
        }));
    }

    @Override public @NotNull CompletableFuture<@UnmodifiableView Collection<PlayerClient>> findAllByName(@NotNull Iterable<String> name) throws NullPointerException
    {
        return findAllByName(name, true);
    }

    @Override protected @NotNull PlayerClient toCachedEntity(@NotNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        return toCachedEntity(UUID.fromString(resultSet.getString("id")), resultSet);
    }

    @Override
    public boolean cached(@NotNull String name) throws NullPointerException
    {
        Collection<PlayerClient> cachedPlayers = cachedEntries().values();
        for (PlayerClient playerClient : cachedPlayers)
        {
            if (Objects.equals(playerClient.username(), name))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public @NotNull CompletableFuture<Void> saveAll(@NotNull Iterable<PlayerClient> id) throws NullPointerException
    {
        Objects.requireNonNull(id, "id cannot be null");
        String sql = "INSERT INTO ksvp_player (id, link_id, username, locale) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE link_id = ?, username = ?, last_login = ?, time_played = ?, locale = ?;";

        return CompletableFuture.supplyAsync(() ->
        {

            // we need to insert missing link ids before
            // this is necessary because the ksvp_player's linkId column refers
            // to the ksvp_identity's link_id column.

            query("INSERT IGNORE INTO ksvp_identity (link_id) VALUES (?);", (statement ->
            {
                for (PlayerClient playerClient : id)
                {
                    String linkId = String.valueOf(playerClient.linkId());
                    statement.setString(1, linkId);
                    statement.addBatch();
                }

                statement.executeBatch();
                return null;
            }));

            return query(sql, (statement ->
            {
                for (PlayerClient playerClient : id)
                {
                    addBatch(statement, playerClient);
                }

                statement.executeBatch();
                return null;
            }));
        });
    }

    /**
     * Adds a {@link PlayerClient} instance to the batch operation of the given {@link PreparedStatement}.
     * This method prepares and populates the SQL parameters for inserting or updating a player's data
     * and then calls {@link PreparedStatement#addBatch()} to include the operation in the batch.
     *
     * @param statement    the {@link PreparedStatement} to which the batch operation is added; must not be null
     * @param playerClient the {@link PlayerClient} instance whose data will populate the SQL parameters; must not be
     *                     null
     * @throws SQLException         if an error occurs while interacting with the {@link PreparedStatement}
     * @throws NullPointerException if the provided {@link PreparedStatement} or {@link PlayerClient} is null
     */
    private void addBatch(@NotNull PreparedStatement statement, @NotNull PlayerClient playerClient) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(statement, "The prepared statement cannot be null.");
        Objects.requireNonNull(playerClient, "The player client cannot be null.");

        statement.setString(1, String.valueOf(playerClient.id()));

        Date lastLogin = Date.valueOf(playerClient.lastLogin().atZone(ZoneId.systemDefault()).toLocalDate());

        setDual(statement, 2, 5, Types.VARCHAR, String.valueOf(playerClient.linkId()));
        setDual(statement, 3, 6, Types.VARCHAR, playerClient.username());

        statement.setDate(7, lastLogin);
        statement.setLong(8, playerClient.timePlayed().get(ChronoUnit.SECONDS));

        setDual(statement, 4, 9, Types.VARCHAR, playerClient.locale().toLanguageTag());

        overrideSignature(playerClient);
        statement.addBatch();
    }
}
