package net.kissenpvp.network.actor.properties;

import net.kissenpvp.api.base.KissenPlugin;
import net.kissenpvp.api.database.SQLExecutor;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.util.concurrent.CompletableFuture;

public class MapExecutor extends SQLExecutor
{
    private final KissenPlugin plugin;

    public MapExecutor(@NonNull DataSource dataSource, @NonNull KissenPlugin plugin) throws NullPointerException
    {
        super(dataSource);
        this.plugin = plugin;
    }

    public @NonNull CompletableFuture<Void> put(@NonNull PersistentPlayerProperty... playerProperty)
    {
        String sql = "INSERT INTO ksvp_player_data (id, plugin, property_key, property_value) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE property_value = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, statement ->
        {
            for(PersistentPlayerProperty property : playerProperty)
            {
                statement.setString(1, String.valueOf(property.player().id()));
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

    public @NonNull CompletableFuture<Void> remove(@NonNull PersistentPlayerProperty... playerProperty)
    {
        String sql = "DELETE FROM ksvp_player_data WHERE id = ? AND plugin = ? AND property_key = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, statement ->
        {
            for(PersistentPlayerProperty property : playerProperty)
            {
                statement.setString(1, String.valueOf(property.player().id()));
                statement.setString(2, plugin.getName());
                statement.setString(3, property.key());
                statement.addBatch();
            }

            statement.executeBatch();
            return null;
        }));
    }
}
