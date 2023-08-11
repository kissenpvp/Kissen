/*
 * Copyright 2023 KissenPvP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.kissenpvp.core.database.jdbc;

import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.ObjectMeta;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.FilterType;
import net.kissenpvp.core.api.database.queryapi.QuerySelect;
import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.api.database.savable.SavableMap;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.savable.KissenSavableMap;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
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
    public void add(@NotNull String id, @Nullable Map<String, String> data) throws BackendException {

        try {
            PreparedStatement deleteStatement = getPreparedStatement("DELETE FROM " + getTable() + " WHERE " + getTotalIDColumn() + " = ?;");
            deleteStatement.setString(1, id);
            spawnThread(id, "", data, mapWriter(), deleteStatement); //TODO key does not worki worki like this
        } catch (SQLException sqlException) {
            throw new BackendException(sqlException);
        }
    }

    @Override
    public @NotNull Optional<@Nullable SavableMap> getData(@NotNull String totalId) {
        Map<String, SavableMap> data = new HashMap<>();
        try {
            data.putAll(processQuery(select(Column.TOTAL_ID, Column.KEY, Column.VALUE).appendFilter(Column.TOTAL_ID, totalId, FilterType.EQUALS)));
        } catch (BackendException sqlException) {
            KissenCore.getInstance().getLogger().error("Could not read data from database. This can lead to unwanted behaviour, it's recommended to shut down the server to prevent any harm to the servers data.", sqlException);
        }
        return Optional.ofNullable(data.get(totalId));
    }

    @Override
    public @Unmodifiable @NotNull <T extends Savable> Map<@NotNull String, @NotNull SavableMap> getData(@NotNull T savable) {
        Map<String, SavableMap> data = new HashMap<>();
        try {
            data.putAll(processQuery(select(Column.TOTAL_ID, Column.KEY, Column.VALUE).appendFilter(Column.TOTAL_ID, savable.getSaveID(),
                    FilterType.START)));
        } catch (BackendException sqlException) {
            KissenCore.getInstance().getLogger().error("Could not read data from database. This can lead to unwanted behaviour, it's recommended to shut down the server to prevent any harm to the servers data.", sqlException);
        }
        return Collections.unmodifiableMap(data);
    }

    private @NotNull Map<String, SavableMap> processQuery(@NotNull QuerySelect querySelect) throws BackendException {
        Map<String, SavableMap> dataContainer = new HashMap<>();
        for (String[] current : execute(querySelect)) {
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

    /**
     * Returns a pure function that writes a map of string key-value pairs to the underlying database table.
     *
     * <p>
     * This method returns a writer that can be used to insert or update a map of string key-value pairs
     * into the database table associated with the current object instance.
     * The map represents the data to be written, where each key-value pair corresponds to a column-value mapping in
     * the table.
     * The totalID1 parameter is used as the identifier for the dataset or table where the data will be written.
     * </p>
     *
     * <p>
     * The returned writer implementation utilizes prepared statements to efficiently execute batch insert operations.
     * It iterates over the provided map, sets the values in the prepared statement based on the key-value pairs,
     * and executes the batch operation to insert the data into the table.
     * </p>
     *
     * @return A non-null writer implementation that can be used to write a map of string key-value pairs to the
     * database table.
     * @see Writer
     * @see PreparedStatement
     * @see #getPreparedStatement(String)
     * @see #getTable()
     */
    @Contract(pure = true)
    private @NotNull Writer<Map<String, String>> mapWriter() {
        return (totalID1, key, value) ->
        {
            PreparedStatement preparedStatement = getPreparedStatement("INSERT INTO " + getTable() + " (uuid, identifier, value) VALUES (?, ?, ?);");

            for (Map.Entry<String, String> entry : value.entrySet()) {
                preparedStatement.setString(1, totalID1);
                preparedStatement.setString(2, entry.getKey());
                preparedStatement.setString(3, entry.getValue());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            preparedStatement.close();
        };
    }
}
