/*
 * Copyright (C) 2023 KissenPvP
 *
 * This program is licensed under the Apache License, Version 2.0.
 *
 * This software may be redistributed and/or modified under the terms
 * of the Apache License as published by the Apache Software Foundation,
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the Apache
 * License, Version 2.0 for the specific language governing permissions
 * and limitations under the License.
 *
 * You should have received a copy of the Apache License, Version 2.0
 * along with this program. If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package net.kissenpvp.core.database.jdbc;

import lombok.SneakyThrows;
import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.ObjectMeta;
import net.kissenpvp.core.api.database.meta.Table;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.select.QuerySelect;
import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.api.database.savable.SavableMap;
import net.kissenpvp.core.database.savable.KissenSavableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Represents a Kissen metaclass for object-based data storage.
 *
 * <p>
 * The {@code KissenObjectMeta} class extends the {@link KissenNativeJDBCMeta} class and implements the
 * {@link ObjectMeta} interface.
 * It provides a meta class specifically designed for storing and retrieving object-based data in the associated
 * database table.
 * </p>
 *
 * <p>
 * To create an instance of {@code KissenObjectMeta}, a table name must be provided. The table will be associated
 * with Kissen objects
 * and used for storing the object data.
 * </p>
 *
 * <p>
 * The {@code KissenObjectMeta} class inherits the constructor from its parent class {@code KissenNativeMeta}, which
 * takes the table
 * name as a parameter. It calls the super constructor to initialize the {@code KissenObjectMeta} object.
 * </p>
 */
public abstract class KissenObjectJDBCMeta extends KissenNativeJDBCMeta implements ObjectMeta {

    /**
     * Constructs a new {@code KissenObjectMeta} object with the specified table name.
     *
     * <p>
     * The {@code KissenObjectMeta} class represents a meta class specifically designed for storing and retrieving
     * object-based data in the associated database table.
     * This constructor creates a new instance of {@code KissenObjectMeta} and associates it with the provided table
     * name.
     * </p>
     *
     * <p>
     * The table specified in the {@code table} parameter will be used to store the object data associated with
     * Kissen objects.
     * </p>
     *
     * <p>
     * The {@code KissenObjectMeta} object extends the {@link KissenNativeJDBCMeta} class and implements the
     * {@link ObjectMeta} interface.
     * By calling the super constructor with the provided table name, the {@code KissenObjectMeta} object is
     * initialized and inherits the behavior and properties
     * defined in its parent class.
     * </p>
     *
     * @param table the name of the table associated with the Kissen objects.
     *              It should be a non-null {@link String} value representing the table name in the database.
     *              The table name is used to uniquely identify the table where object data is stored.
     *              The table name should follow the naming conventions and restrictions of the database system being
     *              used.
     *              Examples of table names can be "objects", "kissen_objects", etc.
     * @throws NullPointerException if the provided table name is {@code null}.
     *                              In such cases, a {@link NullPointerException} will be thrown to indicate the
     *                              invalid input.
     * @see KissenNativeJDBCMeta
     * @see ObjectMeta
     */
    public KissenObjectJDBCMeta(@NotNull Table table, @Nullable KissenPlugin plugin) {
        super(table, plugin);
    }

    @Override
    public void insertJsonMap(@NotNull String id, @NotNull Map<@NotNull String, @NotNull Object> data) throws BackendException {
        getPreparedStatement(String.format("INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?);", getTable(), getTable().getColumn(Column.TOTAL_ID), getTable().getColumn(Column.KEY), getTable().getPluginColumn(), getTable().getTypeColumn(), getTable().getColumn(Column.VALUE)), preparedStatement -> {
            for (Map.Entry<String, Object> current : data.entrySet()) {
                preparedStatement.setString(1, id);
                preparedStatement.setString(2, current.getKey());

                String[] serialized = serialize(current.getValue());
                preparedStatement.setString(3, getPluginName());
                preparedStatement.setString(4, serialized[0]); // type
                preparedStatement.setString(5, serialized[1]); // value
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        });
    }

    @Override
    public @NotNull CompletableFuture<SavableMap> getData(@NotNull String totalId) {
        QuerySelect select = select(Column.KEY, Column.VALUE).where(Column.TOTAL_ID, totalId);
        return select.execute().thenApply(data -> {
            Object[][] modifiedData = new Object[data.length][data[0].length + 1];

            for (int i = 0; i < data.length; i++) {
                modifiedData[i][0] = totalId;
                System.arraycopy(data[i], 0, modifiedData[i], 1, data[i].length);
            }

            return processQuery(modifiedData).get(totalId);
        });
    }

    @Override
    public @NotNull <T extends Savable> CompletableFuture<@Unmodifiable Map<@NotNull String, @NotNull SavableMap>> getData(@NotNull T savable) {
        String regex = "^" + savable.getSaveID();
        return select(Column.TOTAL_ID, Column.KEY, Column.VALUE).where(Column.TOTAL_ID, regex).execute().thenApply(this::processQuery);
    }

    @SneakyThrows
    private @NotNull Map<String, SavableMap> processQuery(@NotNull Object @NotNull [] @NotNull [] data) throws BackendException {
        Map<String, SavableMap> dataContainer = new HashMap<>();
        for (Object[] current : data) {
            Function<String, SavableMap> generateMap = (id) -> new KissenSavableMap(id, KissenObjectJDBCMeta.this);
            dataContainer.computeIfAbsent(current[0].toString(), generateMap).put(current[1].toString(), current[2]);
        }
        return dataContainer;
    }
}
