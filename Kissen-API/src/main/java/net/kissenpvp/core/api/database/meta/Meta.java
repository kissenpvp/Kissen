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

package net.kissenpvp.core.api.database.meta;

import net.kissenpvp.core.api.base.Kissen;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code Meta} interface serves as an interface between a database table and the Java plugin.
 * It is designed to provide a standardized approach for interacting with database tables in a plugin environment.
 *
 * <p>The {@code Meta} interface extends the {@link java.io.Serializable} interface, allowing instances of
 * implementing classes
 * to be serialized and deserialized, enabling their transfer across different JVMs or persistence on disk.
 *
 * <p>Implementing classes should adhere to the following guidelines:
 * <ul>
 *   <li>Implementing classes must provide methods for performing database operations such as querying, inserting,
 *   updating, and deleting records.</li>
 *   <li>Implementing classes should handle the necessary connection management, such as establishing and closing
 *   connections to the database.</li>
 *   <li>Implementing classes should ensure proper exception handling for database-related errors.</li>
 *   <li>Implementing classes should follow the recommended coding conventions and best practices for database
 *   interaction in Java.</li>
 * </ul>
 *
 * <p>By implementing the {@code Meta} interface, plugin developers can leverage a common set of methods and conventions
 * for interacting with database tables in their plugins, promoting code reusability and maintainability.
 *
 * @see java.io.Serializable
 * @see Kissen#getPublicMeta()
 */
public interface Meta extends MetaReader, MetaWriter {

    /**
     * Checks if a meta was stored and exists in the database.
     *
     * <p>
     * This method internally uses the {@link #getString(String, String)} method to retrieve the meta value
     * associated with the specified {@code totalID} and {@code key}.
     * It then performs an {@link java.util.Optional#isPresent()} check on the returned optional value to determine
     * if the meta exists.
     * </p>
     *
     * <p>
     * Note: This method can have performance implications depending on the underlying implementation.
     * It involves retrieving the meta value from the database and performing the existence check.
     * Implementations should be mindful of the potential performance impact, especially in scenarios with frequent
     * calls to this method.
     * </p>
     *
     * @param totalID The ID, which is located under the IDIdentifier.
     * @param key     Under MySQL, it refers to the column, and under files, it serves as the suffix used to
     *                recognize the value.
     * @return {@code true} if the meta exists, {@code false} otherwise.
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     * @see #getString(String, String)
     */
    boolean metaContains(@NotNull String totalID, @NotNull String key) throws BackendException;

    /**
     * Deletes all data associated with the specified {@code totalID} from the table.
     * This operation permanently removes the data, and there is no way to recover it.
     * Extreme caution should be exercised when using this method.
     *
     * <p>
     * Note that this action is executed asynchronously, meaning that the method returns immediately
     * after initiating the deletion process. The deletion process may take some time to complete,
     * depending on the size of the data and the underlying database system.
     * </p>
     *
     * @param totalID The ID of the entry to delete.
     * @throws NullPointerException if {@code totalID} is {@code null}.
     * @see #delete(String, String)
     */
    void purge(@NotNull String totalID) throws BackendException;

    /**
     * Deletes a specific entry from the dataset based on the provided total ID and key.
     *
     * <p>
     * Note that this action is executed asynchronously, meaning that the method returns immediately
     * after initiating the deletion process. The deletion process may take some time to complete,
     * depending on the size of the data and the underlying database system.
     * </p>
     *
     * <p>
     * This method removes a specific entry or record from the dataset that matches the given total ID and key.
     * The total ID uniquely identifies the dataset or table containing the entry,
     * while the key represents the unique identifier of the specific entry to be deleted.
     * </p>
     *
     * @param totalID The total ID of the dataset from which the entry will be deleted. Must not be {@code null}.
     *                The total ID uniquely identifies the dataset or table from which the entry will be removed.
     *                If the total ID is invalid or does not correspond to an existing dataset, no action will be taken.
     * @param key     The key that identifies the entry to be deleted. Must not be {@code null}.
     *                The key represents the unique identifier of the specific entry to be removed.
     *                If the key is invalid or does not exist in the dataset, no action will be taken.
     * @throws NullPointerException If either the total ID or the key is {@code null}.
     * @see #purge(String)
     */
    void delete(@NotNull String totalID, @NotNull String key) throws BackendException;

    /**
     * Deletes all data associated with the specified {@code key} from the table.
     * This operation permanently removes the data, and there is no way to recover it.
     * Extreme caution should be exercised when using this method.
     *
     * @param key The key used to identify the data to be deleted.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @throws BackendException     if a database access error occurs.
     */
    void delete(@NotNull String key) throws BackendException;

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
    @NotNull String getTable();

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
    @NotNull String getTotalIDColumn();
}