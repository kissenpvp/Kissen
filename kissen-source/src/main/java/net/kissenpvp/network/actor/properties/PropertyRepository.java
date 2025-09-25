package net.kissenpvp.network.actor.properties;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kissenpvp.api.base.KissenPlugin;
import net.kissenpvp.api.database.SQLExecutor;
import net.kissenpvp.api.network.actor.PlayerClient;
import net.kissenpvp.api.network.actor.PlayerProperties;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PropertyRepository extends SQLExecutor
{
    private final PlayerClient player;
    private final KissenPlugin plugin;

    public PropertyRepository(@NonNull PlayerClient player, @NonNull KissenPlugin plugin, @NonNull DataSource dataSource) throws NullPointerException
    {
        super(dataSource);
        this.player = player;
        this.plugin = plugin;
    }

    public @NonNull CompletableFuture<Void> put(@NonNull Iterable<PlayerProperty> playerProperty)
    {
        String sql = "INSERT INTO ksvp_player_data (id, plugin, property_key, property_value) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE property_value = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, statement ->
        {
            for(PlayerProperty property : playerProperty)
            {
                statement.setString(1, String.valueOf(player.id()));
                statement.setString(2, plugin.getName());
                statement.setString(3, property.key());
                statement.setString(4, String.valueOf(property.value()));
                statement.setString(5, String.valueOf(property.value()));
                statement.addBatch();
            }

            statement.executeBatch();
            return null;
        }));
    }

    public @NonNull CompletableFuture<PlayerProperties> find()
    {
        String sql = "SELECT property_key, property_value FROM ksvp_player_data WHERE id = ? AND plugin = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, statement ->
        {
            statement.setString(1, String.valueOf(player.id()));
            statement.setString(2, plugin.getName());

            Map<String, JsonObject> map = new HashMap<>();
            try (ResultSet resultSet = statement.executeQuery())
            {
                JsonObject value = JsonParser.parseString(resultSet.getString("property_value")).getAsJsonObject();
                map.put(resultSet.getString("property_key"), value);
            }

            return new PlayerPropertyMap(PropertyRepository.this, map);
        }));
    }

    public @NonNull CompletableFuture<Void> remove(@NonNull Iterable<PlayerProperty> playerProperty)
    {
        String sql = "DELETE FROM ksvp_player_data WHERE id = ? AND plugin = ? AND property_key = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, statement ->
        {
            for(PlayerProperty property : playerProperty)
            {
                statement.setString(1, String.valueOf(player.id()));
                statement.setString(2, plugin.getName());
                statement.setString(3, property.key());
                statement.addBatch();
            }

            statement.executeBatch();
            return null;
        }));
    }

    public @NonNull KissenPlugin plugin()
    {
        return plugin;
    }

    public @NonNull PlayerClient player()
    {
        return player;
    }
}
