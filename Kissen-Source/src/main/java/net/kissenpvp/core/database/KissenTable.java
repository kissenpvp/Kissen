package net.kissenpvp.core.database;

import lombok.RequiredArgsConstructor;
import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import net.kissenpvp.core.api.database.meta.Meta;
import net.kissenpvp.core.api.database.meta.Table;
import net.kissenpvp.core.api.database.queryapi.Column;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public abstract class KissenTable implements Table {

    private final String table, totalIDColumn, keyColumn, pluginColum, typeColumn, valueColumn;

    @Override
    public @NotNull Meta registerMeta(@NotNull KissenPlugin kissenPlugin) {
        return setupMeta(kissenPlugin);
    }

    public abstract @NotNull Meta setupMeta(@Nullable KissenPlugin kissenPlugin);

    @Override
    public @NotNull String getTable() {
        return table;
    }

    @Override
    public @NotNull String getColumn(@NotNull Column column) {
        return switch (column) {
            case TOTAL_ID -> totalIDColumn;
            case KEY -> keyColumn;
            case VALUE -> valueColumn;
        };
    }

    @Override
    public @NotNull String getTypeColumn() {
        return typeColumn;
    }

    @Override
    public @NotNull String getPluginColumn() {
        return pluginColum;
    }

    @Override
    public @NotNull String toString() {
        return getTable();
    }
}
