package net.kissenpvp.core.api.database.meta;

import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import net.kissenpvp.core.api.database.queryapi.Column;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public interface Table extends Serializable {

    @NotNull
    Meta registerMeta(@NotNull KissenPlugin kissenPlugin);

    /**
     * Retrieves the name of the table associated with the meta.
     *
     * <p>
     * This method returns the name of the database table to which the meta is associated.
     * The table name can be useful for various operations or reference purposes.
     * </p>
     *
     * <p>
     * Note: The table name returned by this method represents the actual name used in the database.
     * It may differ from any table alias or logical name used in the application layer.
     * </p>
     *
     * @return The name of the table.
     */
    @NotNull
    String getTable();

    /**
     * Retrieves the ID column associated with this object.
     *
     * <p>
     * The ID column represents a unique identifier for each entry in the dataset.
     * It is used to uniquely identify and reference individual records or data points.
     * </p>
     *
     * @return The name of the ID column as a {@code String}.
     * @throws NullPointerException If the ID column is {@code null}.
     */
    @NotNull
    String getColumn(@NotNull Column column);

    @NotNull
    String getTypeColumn();

    @NotNull
    String getPluginColumn();

}
