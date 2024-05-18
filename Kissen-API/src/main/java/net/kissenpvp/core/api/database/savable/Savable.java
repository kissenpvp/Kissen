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

package net.kissenpvp.core.api.database.savable;

import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import net.kissenpvp.core.api.database.meta.Table;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * The {@code Savable} interface extends the {@link SavableMap} interface and represents an interface to a database
 * that supports automatic synchronization of data. It provides methods to manipulate and access key-value pairs
 * that are automatically synced with the underlying database.
 *
 * <p>The {@code Savable} interface inherits all the methods from the {@code SavableMap} interface, which extends the
 * {@link Map} interface and adds additional functionality specific to saving and retrieving values.</p>
 *
 * <p>Implementations of the {@code Savable} interface should provide implementations for all the methods inherited from
 * the {@code SavableMap} interface and the {@code Map} interface.</p>
 *
 * <p>Key-value pairs stored in a {@code Savable} implementation are automatically synchronized with the underlying
 * database, ensuring that changes made to the data are persisted and available across different sessions or
 * applications.</p>
 *
 * <p>Implementations of the {@code Savable} interface may support additional functionality or provide customized
 * behavior
 * beyond the methods defined in the {@code SavableMap} and {@code Map} interfaces. It is recommended to refer to the
 * documentation of the specific implementation class for more details on the behavior and usage of the methods provided
 * by that implementation.</p>
 *
 * @see SavableMap
 * @see Map
 */
public interface Savable<T> {

    /**
     * Retrieves the save ID for the object. The save ID is used to recognize and distinguish the object from other
     * objects
     * in the same database. It is typically appended in front of the {@link #getRawID()} value.
     *
     * <p>The save ID is a unique identifier associated with the object. It helps in maintaining uniqueness and
     * providing
     * a clear distinction between different objects of similar types, such as users or ranks, that coexist within the
     * database.</p>
     *
     * <p>The save ID is typically a string value that can be easily identified and associated with the object. It is
     * often
     * used in combination with the {@link #getRawID()} value to construct a complete and unique identifier for the
     * object.</p>
     *
     * <p>It is important to note that the save ID is specific to the object and its purpose is internal to the database
     * implementation. It may not have any significance outside the database context.</p>
     *
     * <p>To retrieve the complete identifier for the object, including the save ID and the raw ID, you can concatenate
     * the save ID with the raw ID value obtained from {@link #getRawID()}.</p>
     *
     * <p>Implementations of this method should return a non-null string value representing the save ID associated with
     * the object.</p>
     *
     * @return the save ID associated with the object
     * @see #getRawID()
     * @see #getDatabaseID()
     */

    @NotNull
    String getSaveID();

    @NotNull
    Table getTable();

    @NotNull
    SavableMap getRepository(@NotNull KissenPlugin plugin);

    /**
     * Retrieves the unmodified ID associated with the object. The raw ID represents the original identifier given to
     * the
     * object, such as the UUID of a user or the name of a permission group.
     *
     * <p>The raw ID is the unaltered form of the object's identifier, as provided during the setup or creation process.
     * It is typically used in conjunction with other attributes or methods to uniquely identify the object within the
     * database.</p>
     *
     * <p>The raw ID is often utilized in combination with other information to create a complete and unique identifier
     * for the object. It may undergo further processing or transformation, such as concatenation or parsing, to
     * generate
     * a comprehensive identification value.</p>
     *
     * <p>It is important to note that the raw ID represents the original form of the object's identifier and may have
     * certain constraints or requirements depending on the database or system. Care should be taken to ensure that the
     * raw ID adheres to the necessary specifications.</p>
     *
     * <p>This method should return the unmodified ID associated with the object, as parsed or provided during the setup
     * or creation process. The returned value should not be null.</p>
     *
     * @return the unmodified ID associated with the object
     * @see #getSaveID()
     * @see #getDatabaseID()
     */
    @NotNull
    T getRawID();

    /**
     * Retrieves the ID that is used to save the entry to the table. The ID is constructed by concatenating the
     * {@link #getSaveID()} and {@link #getRawID()} values together.
     *
     * <p>The ID represents a unique identifier for the entry that encompasses all the necessary data in the table.
     * By combining the save ID and raw ID, a comprehensive identification value is created.</p>
     *
     * <p>The ID is typically used to uniquely identify the entry within the table and may serve as a key or reference
     * for retrieval and manipulation operations.</p>
     *
     * <p>It is important to note that the ID generated by this method is specific to the implementation and is used for
     * internal purposes related to saving and retrieving data from the table.</p>
     *
     * <p>This method returns a non-null string value representing the ID associated with the entry in the table.</p>
     *
     * @return the ID that contains all the necessary data in the table
     * @see #getSaveID()
     * @see #getRawID()
     */
    @NotNull String getDatabaseID();

    /**
     * Retrieves the keys that must be set in any case. These keys represent the required values that must be set
     * for the object to be considered valid.
     *
     * <p>The returned keys provide information about the specific attributes or properties that must be present
     * in order to ensure the object's completeness and correctness. It is essential to set these keys to fulfill
     * the requirements and constraints of the implementation.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * String[] keys = object.getKeys();
     * System.out.println(Arrays.toString(keys)); // Output: ["name", "age", "address"]
     * }
     * </pre>
     *
     * <p>The keys represent the essential elements that must be populated or assigned meaningful values in order
     * for the object to be considered valid and complete within the context of the implementation.</p>
     *
     * <p>It is important to note that the returned keys are specific to the implementation and may vary depending
     * on the requirements and design of the object. These keys provide valuable information about the mandatory
     * fields or attributes that need to be provided.</p>
     *
     * <p>This method should return a non-null array of string values representing the keys that must be set in
     * any case. The array should not be modified after retrieval.</p>
     *
     * @return the keys that must be set in any case
     * @see #setup(Object, Map)
     */
    @NotNull
    String[] getKeys();


    @Contract(value = "_ -> this")
    @NotNull Savable<T> setup(@NotNull T id) throws SavableInitializeException;

    @Contract(value = "_, _ -> this")
    @NotNull Savable<T> setup(@NotNull T id, @Nullable Map<String, Object> initialData) throws SavableInitializeException;

    /**
     * Retrieves the storage map associated with the savable object. This map serves as a cache for storing
     * session-specific data and is cleared whenever the savable object is no longer in use.
     *
     * <p>The storage map provides a convenient way to store and retrieve session data that is specific to the
     * savable object. It can be used to hold temporary or transient information related to the object's current
     * state or context. The map is designed to be cleared automatically when the object is no longer actively used
     * to free up resources and ensure data integrity.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * Map (String, Object) storage = object.getStorage();
     * storage.put("sessionKey", "sessionValue");
     * Object value = storage.get("sessionKey");
     * System.out.println(value); // Output: "sessionValue"
     * }
     * </pre>
     *
     * <p>This method should return a non-null {@link Map} instance representing the storage map associated
     * with the savable object. The map can be used to store and retrieve session-specific data. It is important to
     * understand that the map may be cleared automatically when the object is no longer actively used.</p>
     *
     * @return the storage map associated with the savable object
     * @see #setup(Object, Map)
     */
    @NotNull
    Map<String, Object> getStorage();

    /**
     * Deletes the savable object from the database and returns the number of rows affected by this operation.
     *
     * <p>The delete operation removes the savable object and its associated data from the database. It returns
     * the number of rows that were affected by this deletion, providing information about the impact of the
     * operation.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * int rowsAffected = object.delete();
     * System.out.println("Rows affected: " + rowsAffected);
     * }
     * </pre>
     *
     * <p>The delete operation is performed on the underlying database table or collection, removing the entry
     * associated with the savable object. The return value indicates the number of rows or documents affected
     * by this deletion. It can be used to determine the success and impact of the deletion.</p>
     *
     * <p>It's important to note that the delete operation permanently removes the object and its data from the
     * database. This action is not reversible, and caution should be exercised when using this method.</p>
     *
     * <p>This method should be called when the intention is to delete the savable object from the database. It returns
     * an integer value indicating the number of rows affected by this deletion.</p>
     *
     * @return the number of rows affected by the delete operation
     * @see #setup(Object, Map)
     */
    int delete();
}
