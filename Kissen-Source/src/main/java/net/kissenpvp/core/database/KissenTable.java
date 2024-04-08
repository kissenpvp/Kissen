package net.kissenpvp.core.database;

import lombok.Getter;
import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import net.kissenpvp.core.api.database.meta.ObjectMeta;
import net.kissenpvp.core.api.database.meta.Table;
import net.kissenpvp.core.api.database.queryapi.Column;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class KissenTable implements Table {

    private final String table, totalIDColumn, keyColumn, pluginColum, typeColumn, valueColumn;
    @Getter private final ObjectMeta internal;

    public KissenTable(@NotNull String table, @NotNull String totalIDColumn, @NotNull String keyColumn, @NotNull String pluginColum, @NotNull String typeColumn, @NotNull String valueColumn) {
        this.table = table;
        this.totalIDColumn = totalIDColumn;
        this.keyColumn = keyColumn;
        this.pluginColum = pluginColum;
        this.typeColumn = typeColumn;
        this.valueColumn = valueColumn;
        this.internal = createMeta(this, null);
    }

    @Override
    public @NotNull ObjectMeta registerMeta(@NotNull KissenPlugin kissenPlugin) {
        return createMeta(this, kissenPlugin);
    }

    protected abstract @NotNull ObjectMeta createMeta(@NotNull Table instance, @Nullable KissenPlugin kissenPlugin);

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
