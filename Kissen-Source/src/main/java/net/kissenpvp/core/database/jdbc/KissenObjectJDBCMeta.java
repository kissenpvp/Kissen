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

import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.ObjectMeta;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.FilterType;
import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.api.database.savable.SavableMap;
import net.kissenpvp.core.database.savable.KissenSavableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    public KissenObjectJDBCMeta(@NotNull String table) {
        super(table);
    }

    @Override
    public void add(@NotNull String id, @NotNull Map<String, String> data) throws BackendException {
        getPreparedStatement(String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?);", getTable(), getTotalIDColumn(), getKeyColumn(), getValueColumn()),
                preparedStatement -> {
                    for(Map.Entry<String, String> current : data.entrySet())
                    {
                        preparedStatement.setString(1, id);
                        preparedStatement.setString(2, current.getKey());
                        preparedStatement.setString(3, current.getValue());
                        preparedStatement.addBatch();
                    }
                    preparedStatement.executeBatch();
                });
    }

    @Override
    public @NotNull Optional<SavableMap> getData(@NotNull String totalId)  {
        String[][] data = select(Column.TOTAL_ID, Column.KEY, Column.VALUE).where(Column.TOTAL_ID, totalId, FilterType.EQUALS).execute();
        return Optional.ofNullable(processQuery(data).get(totalId));
    }

    @Override
    public @Unmodifiable @NotNull <T extends Savable> Map<@NotNull String, @NotNull SavableMap> getData(@NotNull T savable) {
        String[][] data = select(Column.TOTAL_ID, Column.KEY, Column.VALUE).where(Column.TOTAL_ID, savable.getSaveID(), FilterType.START).execute();
        return Map.copyOf(processQuery(data));
    }

    private @NotNull Map<String, SavableMap> processQuery(@NotNull String[] @NotNull [] data) throws BackendException {
        Map<String, SavableMap> dataContainer = new HashMap<>();
        for (String[] current : data) {
            String totalID = current[0], key = current[1], value = current[2];

            dataContainer.putIfAbsent(totalID, new KissenSavableMap(totalID, KissenObjectJDBCMeta.this));
            SavableMap savableMap = dataContainer.get(totalID);
            if (key.startsWith("_")) {
                savableMap.putListValue(key.substring(1), value);
            } else {
                savableMap.put(key, value);
            }
            dataContainer.remove(totalID);
            dataContainer.put(totalID, savableMap);
        }
        return dataContainer;
    }
}
