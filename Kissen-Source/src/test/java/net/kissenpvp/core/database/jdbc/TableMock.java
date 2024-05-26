package net.kissenpvp.core.database.jdbc;

import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import net.kissenpvp.core.api.database.meta.Meta;
import net.kissenpvp.core.api.database.meta.Table;
import net.kissenpvp.core.api.database.queryapi.Column;
import org.jetbrains.annotations.NotNull;

public class TableMock implements Table {
    @Override
    public @NotNull Meta registerMeta(@NotNull KissenPlugin kissenPlugin) {
        return null;
    }

    @Override
    public @NotNull String getTable() {
        return "test";
    }

    @Override
    public @NotNull String getColumn(@NotNull Column column) {
        return column.toString().toLowerCase();
    }

    @Override
    public @NotNull String getTypeColumn() {
        return "type";
    }

    @Override
    public @NotNull String getPluginColumn() {
        return "plugin";
    }

    @Override
    public String toString() {
        return getTable();
    }
}
