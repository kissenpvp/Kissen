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

import net.kissenpvp.core.api.database.queryapi.update.QueryUpdate;
import net.kissenpvp.core.api.database.queryapi.update.QueryUpdateDirective;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Collection;

/**
 * The MetaWriter interface represents a writer for storing meta values in a database.
 * It extends the Serializable interface, indicating that implementations of this interface can be serialized.
 *
 * <p>
 * The MetaWriter interface provides methods for storing various types of values in the meta table.
 * The methods operate asynchronously, allowing for non-blocking execution while storing the values in the database.
 * It is important to handle any exceptions that may occur during the storage process appropriately.
 * </p>
 *
 * <p>
 * Note: All the methods in this interface may throw a BackendException if there is an error during the database storage
 * process.
 * It is important to handle this exception appropriately when using the MetaWriter interface.
 * </p>
 *
 * @see Serializable
 * @see MetaReader
 */
public interface MetaWriter extends Serializable {

    /**
     * Sets a value in the meta associated with the specified {@code totalID} and {@code key}.
     * <p>
     * The {@code totalID} parameter is used to identify the specific meta, and the {@code key} parameter is used
     * to identify the specific value within the meta. The {@code value} parameter represents the value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * If the {@code value} parameter is {@code null}, it indicates that the corresponding entry should be deleted
     * from the meta.
     * </p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * Meta meta = new SomeMeta();
     * String totalID = "exampleTotalID";
     * String key = "exampleKey";
     * String value = "exampleValue";
     *
     * // Set a value in the meta associated with the specified totalID and key
     * meta.setString(totalID, key, value);
     * }
     * </pre>
     *
     * @param totalID The identifier for the meta in which the value is to be set.
     * @param key     The key to identify the value within the meta.
     * @param value   The value to be saved, or {@code null} to delete the corresponding entry from the meta.
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     * @throws BackendException     if an error occurs during the backend operation.
     * @see #setString(String, String)
     * @see MetaReader#getString(String)
     * @see MetaReader#getString(String, String)
     */
    void setString(@NotNull String totalID, @NotNull String key, @Nullable String value) throws BackendException;

    /**
     * Sets a value in the meta associated with the specified {@code key}.
     * <p>
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * If the {@code value} parameter is {@code null}, it indicates that the corresponding entry should be deleted
     * from the meta.
     * </p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * Meta meta = new SomeMeta();
     * String key = "exampleKey";
     * String value = "exampleValue";
     *
     * // Set a value in the meta associated with the specified key
     * meta.setString(key, value);
     * }
     * </pre>
     *
     * @param key   The key to identify the value within the meta.
     * @param value The value to be saved, or {@code null} to delete the corresponding entry from the meta.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @throws BackendException     if an error occurs during the backend operation.
     * @see #setString(String, String, String)
     * @see MetaReader#getString(String)
     * @see MetaReader#getString(String, String)
     */
    void setString(@NotNull String key, @Nullable String value) throws BackendException;

    /**
     * Sets a long value in the meta associated with the specified {@code totalID} and {@code key}.
     * <p>
     * The {@code totalID} parameter is used to identify the specific meta, and the {@code key} parameter is used
     * to identify the specific value within the meta. The {@code value} parameter represents the long value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * If the {@code value} parameter is {@code null}, it indicates that the corresponding entry should be deleted
     * from the meta.
     * </p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * Meta meta = new SomeMeta();
     * String totalID = "exampleTotalID";
     * String key = "exampleKey";
     * long value = 123456L;
     *
     * // Set a long value in the meta associated with the specified totalID and key
     * meta.setLong(totalID, key, value);
     * }
     * </pre>
     *
     * @param totalID The identifier for the meta in which the value is to be set.
     * @param key     The key to identify the value within the meta.
     * @param value   The long value to be saved.
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     * @throws BackendException     if an error occurs during the backend operation.
     * @see #setLong(String, long)
     * @see MetaReader#getLong(String)
     * @see MetaReader#getLong(String, String)
     */
    void setLong(@NotNull String totalID, @NotNull String key, long value) throws BackendException;

    /**
     * Sets a long value in the meta associated with the specified {@code key}.
     * <p>
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the long value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * If the {@code value} parameter is {@code null}, it indicates that the corresponding entry should be deleted
     * from the meta.
     * </p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * Meta meta = new SomeMeta();
     * String key = "exampleKey";
     * long value = 123456L;
     *
     * // Set a long value in the meta associated with the specified key
     * meta.setLong(key, value);
     * }
     * </pre>
     *
     * @param key   The key to identify the value within the meta.
     * @param value The long value to be saved.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @throws BackendException     if an error occurs during the backend operation.
     * @see #setLong(String, String, long)
     * @see MetaReader#getLong(String)
     * @see MetaReader#getLong(String, String)
     */
    void setLong(@NotNull String key, long value) throws BackendException;

    /**
     * Sets a double value in the meta associated with the specified {@code totalID} and {@code key}.
     * <p>
     * The {@code totalID} parameter is used to identify the specific meta, and the {@code key} parameter is used
     * to identify the specific value within the meta. The {@code value} parameter represents the double value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * If the {@code value} parameter is {@code null}, it indicates that the corresponding entry should be deleted
     * from the meta.
     * </p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * Meta meta = new SomeMeta();
     * String totalID = "exampleTotalID";
     * String key = "exampleKey";
     * double value = 123.456;
     *
     * // Set a double value in the meta associated with the specified totalID and key
     * meta.setDouble(totalID, key, value);
     * }
     * </pre>
     *
     * @param totalID The identifier for the meta in which the value is to be set.
     * @param key     The key to identify the value within the meta.
     * @param value   The double value to be saved.
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     * @throws BackendException     if an error occurs during the backend operation.
     * @see #setDouble(String, double)
     * @see MetaReader#getDouble(String)
     * @see MetaReader#getDouble(String, String)
     */
    void setDouble(@NotNull String totalID, @NotNull String key, double value) throws BackendException;

    /**
     * Sets a double value in the meta associated with the specified {@code key}.
     * <p>
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the double value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * If the {@code value} parameter is {@code null}, it indicates that the corresponding entry should be deleted
     * from the meta.
     * </p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * Meta meta = new SomeMeta();
     * String key = "exampleKey";
     * double value = 123.456;
     *
     * // Set a double value in the meta associated with the specified key
     * meta.setDouble(key, value);
     * }
     * </pre>
     *
     * @param key   The key to identify the value within the meta.
     * @param value The double value to be saved.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @throws BackendException     if an error occurs during the backend operation.
     * @see #setDouble(String, String, double)
     * @see MetaReader#getDouble(String)
     * @see MetaReader#getDouble(String, String)
     */
    void setDouble(@NotNull String key, double value) throws BackendException;

    /**
     * Sets a float value in the meta associated with the specified {@code totalID} and {@code key}.
     * <p>
     * The {@code totalID} parameter is used to identify the specific meta, and the {@code key} parameter is used
     * to identify the specific value within the meta. The {@code value} parameter represents the float value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * If the {@code value} parameter is {@code null}, it indicates that the corresponding entry should be deleted
     * from the meta.
     * </p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * Meta meta = new SomeMeta();
     * String totalID = "exampleTotalID";
     * String key = "exampleKey";
     * float value = 123.456f;
     *
     * // Set a float value in the meta associated with the specified totalID and key
     * meta.setFloat(totalID, key, value);
     * }
     * </pre>
     *
     * @param totalID The identifier for the meta in which the value is to be set.
     * @param key     The key to identify the value within the meta.
     * @param value   The float value to be saved.
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     * @throws BackendException     if an error occurs during the backend operation.
     * @see #setFloat(String, float)
     * @see MetaReader#getFloat(String)
     * @see MetaReader#getFloat(String, String)
     */
    void setFloat(@NotNull String totalID, @NotNull String key, float value) throws BackendException;

    /**
     * Sets a float value in the meta associated with the specified {@code key}.
     * <p>
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the float value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * If the {@code value} parameter is {@code null}, it indicates that the corresponding entry should be deleted
     * from the meta.
     * </p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * Meta meta = new SomeMeta();
     * String key = "exampleKey";
     * float value = 123.456f;
     *
     * // Set a float value in the meta associated with the specified key
     * meta.setFloat(key, value);
     * }
     * </pre>
     *
     * @param key   The key to identify the value within the meta.
     * @param value The float value to be saved.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @throws BackendException     if an error occurs during the backend operation.
     * @see #setFloat(String, String, float)
     * @see MetaReader#getFloat(String)
     * @see MetaReader#getFloat(String, String)
     */
    void setFloat(@NotNull String key, float value) throws BackendException;

    /**
     * Sets an integer value in the meta associated with the specified {@code totalID} and {@code key}.
     * <p>
     * The {@code totalID} parameter is used to identify the specific meta, and the {@code key} parameter is used
     * to identify the specific value within the meta. The {@code value} parameter represents the integer value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * If the {@code value} parameter is {@code null}, it indicates that the corresponding entry should be deleted
     * from the meta.
     * </p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * Meta meta = new SomeMeta();
     * String totalID = "exampleTotalID";
     * String key = "exampleKey";
     * int value = 123;
     *
     * // Set an integer value in the meta associated with the specified totalID and key
     * meta.setInt(totalID, key, value);
     * }
     * </pre>
     *
     * @param totalID The identifier for the meta in which the value is to be set.
     * @param key     The key to identify the value within the meta.
     * @param value   The integer value to be saved.
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     * @throws BackendException     if an error occurs during the backend operation.
     * @see #setInt(String, int)
     * @see MetaReader#getInt(String)
     * @see MetaReader#getInt(String, String)
     */
    void setInt(@NotNull String totalID, @NotNull String key, int value) throws BackendException;

    /**
     * Sets an integer value in the meta associated with the specified {@code key}.
     * <p>
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the integer value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * If the {@code value} parameter is {@code null}, it indicates that the corresponding entry should be deleted
     * from the meta.
     * </p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * Meta meta = new SomeMeta();
     * String key = "exampleKey";
     * int value = 123;
     *
     * // Set an integer value in the meta associated with the specified key
     * meta.setInt(key, value);
     * }
     * </pre>
     *
     * @param key   The key to identify the value within the meta.
     * @param value The integer value to be saved.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @throws BackendException     if an error occurs during the backend operation.
     * @see #setInt(String, String, int)
     * @see MetaReader#getInt(String)
     * @see MetaReader#getInt(String, String)
     */
    void setInt(@NotNull String key, int value) throws BackendException;

    /**
     * Sets a short value in the meta associated with the specified {@code totalID} and {@code key}.
     * <p>
     * The {@code totalID} parameter is used to identify the specific meta, and the {@code key} parameter is used
     * to identify the specific value within the meta. The {@code value} parameter represents the short value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * If the {@code value} parameter is {@code null}, it indicates that the corresponding entry should be deleted
     * from the meta.
     * </p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * Meta meta = new SomeMeta();
     * String totalID = "exampleTotalID";
     * String key = "exampleKey";
     * short value = 123;
     *
     * // Set a short value in the meta associated with the specified totalID and key
     * meta.setShort(totalID, key, value);
     * }
     * </pre>
     *
     * @param totalID The identifier for the meta in which the value is to be set.
     * @param key     The key to identify the value within the meta.
     * @param value   The short value to be saved.
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     * @throws BackendException     if an error occurs during the backend operation.
     * @see #setShort(String, short)
     * @see MetaReader#getShort(String)
     * @see MetaReader#getShort(String, String)
     */
    void setShort(@NotNull String totalID, @NotNull String key, short value) throws BackendException;

    /**
     * Sets a short value in the meta associated with the specified {@code key}.
     * <p>
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the short value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * If the {@code value} parameter is {@code null}, it indicates that the corresponding entry should be deleted
     * from the meta.
     * </p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * Meta meta = new SomeMeta();
     * String key = "exampleKey";
     * short value = 123;
     *
     * // Set a short value in the meta associated with the specified key
     * meta.setShort(key, value);
     * }
     * </pre>
     *
     * @param key   The key to identify the value within the meta.
     * @param value The short value to be saved.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @throws BackendException     if an error occurs during the backend operation.
     * @see #setShort(String, String, short)
     * @see MetaReader#getShort(String)
     * @see MetaReader#getShort(String, String)
     */
    void setShort(@NotNull String key, short value) throws BackendException;

    /**
     * Sets a byte value in the meta associated with the specified {@code totalID} and {@code key}.
     * <p>
     * The {@code totalID} parameter is used to identify the specific meta, and the {@code key} parameter is used
     * to identify the specific value within the meta. The {@code value} parameter represents the byte value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * If the {@code value} parameter is {@code null}, it indicates that the corresponding entry should be deleted
     * from the meta.
     * </p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * Meta meta = new SomeMeta();
     * String totalID = "exampleTotalID";
     * String key = "exampleKey";
     * byte value = 42;
     *
     * // Set a byte value in the meta associated with the specified totalID and key
     * meta.setByte(totalID, key, value);
     * }
     * </pre>
     *
     * @param totalID The identifier for the meta in which the value is to be set.
     * @param key     The key to identify the value within the meta.
     * @param value   The byte value to be saved.
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     * @throws BackendException     if an error occurs during the backend operation.
     * @see #setByte(String, byte)
     * @see MetaReader#getByte(String)
     * @see MetaReader#getByte(String, String)
     */
    void setByte(@NotNull String totalID, @NotNull String key, byte value) throws BackendException;

    /**
     * Sets a byte value in the meta associated with the specified {@code key}.
     * <p>
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the byte value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * If the {@code value} parameter is {@code null}, it indicates that the corresponding entry should be deleted
     * from the meta.
     * </p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * Meta meta = new SomeMeta();
     * String key = "exampleKey";
     * byte value = 42;
     *
     * // Set a byte value in the meta associated with the specified key
     * meta.setByte(key, value);
     * }
     * </pre>
     *
     * @param key   The key to identify the value within the meta.
     * @param value The byte value to be saved.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @throws BackendException     if an error occurs during the backend operation.
     * @see #setByte(String, String, byte)
     * @see MetaReader#getByte(String)
     * @see MetaReader#getByte(String, String)
     */
    void setByte(@NotNull String key, byte value) throws BackendException;

    /**
     * Sets a boolean value in the meta associated with the specified {@code totalID} and {@code key}.
     * <p>
     * The {@code totalID} parameter is used to identify the specific meta, and the {@code key} parameter is used
     * to identify the specific value within the meta. The {@code value} parameter represents the boolean value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * If the {@code value} parameter is {@code null}, it indicates that the corresponding entry should be deleted
     * from the meta.
     * </p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * Meta meta = new SomeMeta();
     * String totalID = "exampleTotalID";
     * String key = "exampleKey";
     * boolean value = true;
     *
     * // Set a boolean value in the meta associated with the specified totalID and key
     * meta.setBoolean(totalID, key, value);
     * }
     * </pre>
     *
     * @param totalID The identifier for the meta in which the value is to be set.
     * @param key     The key to identify the value within the meta.
     * @param value   The boolean value to be saved.
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     * @throws BackendException     if an error occurs during the backend operation.
     * @see #setBoolean(String, boolean)
     * @see MetaReader#getBoolean(String)
     * @see MetaReader#getBoolean(String, String)
     */
    void setBoolean(@NotNull String totalID, @NotNull String key, boolean value) throws BackendException;

    /**
     * Sets a boolean value in the meta associated with the specified {@code key}.
     * <p>
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the boolean value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * If the {@code value} parameter is {@code null}, it indicates that the corresponding entry should be deleted
     * from the meta.
     * </p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * Meta meta = new SomeMeta();
     * String key = "exampleKey";
     * boolean value = true;
     *
     * // Set a boolean value in the meta associated with the specified key
     * meta.setBoolean(key, value);
     * }
     * </pre>
     *
     * @param key   The key to identify the value within the meta.
     * @param value The boolean value to be saved.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @throws BackendException     if an error occurs during the backend operation.
     * @see #setBoolean(String, String, boolean)
     * @see MetaReader#getBoolean(String)
     * @see MetaReader#getBoolean(String, String)
     */
    void setBoolean(@NotNull String key, boolean value) throws BackendException;

    /**
     * Sets a collection value in the meta associated with the specified {@code totalID} and {@code key}.
     * <p>
     * The {@code totalID} parameter is used to identify the specific meta, and the {@code key} parameter is used
     * to identify the specific value within the meta. The {@code value} parameter represents the collection to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * If the {@code value} parameter is {@code null}, it indicates that the corresponding entry should be deleted
     * from the meta.
     * </p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * Meta meta = new SomeMeta();
     * String totalID = "exampleTotalID";
     * String key = "exampleKey";
     * Collection<?> value = Arrays.asList("item1", "item2", "item3");
     *
     * // Set a collection value in the meta associated with the specified totalID and key
     * meta.setCollection(totalID, key, value);
     * }
     * </pre>
     *
     * @param totalID The identifier for the meta in which the value is to be set.
     * @param key     The key to identify the value within the meta.
     * @param value   The collection to be saved, or {@code null} to delete the corresponding entry from the meta.
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     * @throws BackendException     if an error occurs during the backend operation.
     * @see #setCollection(String, Collection)
     * @see MetaReader#getCollection(String, Class)
     * @see MetaReader#getCollection(String, String, Class)
     */
    void setCollection(@NotNull String totalID, @NotNull String key, @Nullable Collection<?> value) throws BackendException;

    /**
     * Sets a collection value in the meta associated with the specified {@code key}.
     * <p>
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the collection to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * If the {@code value} parameter is {@code null}, it indicates that the corresponding entry should be deleted
     * from the meta.
     * </p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * Meta meta = new SomeMeta();
     * String key = "exampleKey";
     * Collection<?> value = Arrays.asList("item1", "item2", "item3");
     *
     * // Set a collection value in the meta associated with the specified key
     * meta.setCollection(key, value);
     * }
     * </pre>
     *
     * @param key   The key to identify the value within the meta.
     * @param value The collection to be saved, or {@code null} to delete the corresponding entry from the meta.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @throws BackendException     if an error occurs during the backend operation.
     * @see #setCollection(String, String, Collection)
     * @see MetaReader#getCollection(String, Class)
     * @see MetaReader#getCollection(String, String, Class)
     */
    void setCollection(@NotNull String key, @Nullable Collection<?> value) throws BackendException;

    /**
     * Sets an object value in the meta associated with the specified {@code totalID} and {@code key}.
     * <p>
     * The {@code totalID} parameter is used to identify the specific meta, and the {@code key} parameter is used
     * to identify the specific value within the meta. The {@code value} parameter represents the object to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * If the {@code value} parameter is {@code null}, it indicates that the corresponding entry should be deleted
     * from the meta.
     * </p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * Meta meta = new SomeMeta();
     * String totalID = "exampleTotalID";
     * String key = "exampleKey";
     * SomeObject value = new SomeObject();
     *
     * // Set an object value in the meta associated with the specified totalID and key
     * meta.setObject(totalID, key, value);
     * }
     * </pre>
     *
     * @param totalID The identifier for the meta in which the value is to be set.
     * @param key     The key to identify the value within the meta.
     * @param value   The object to be saved, or {@code null} to delete the corresponding entry from the meta.
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     * @throws BackendException     if an error occurs during the backend operation.
     * @see #setObject(String, Object)
     * @see MetaReader#getObject(String, Class)
     * @see MetaReader#getObject(String, String, Class)
     */
    <T> void setObject(@NotNull String totalID, @NotNull String key, @Nullable T value) throws BackendException;

    /**
     * Sets an object value in the meta associated with the specified {@code key}.
     * <p>
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the object to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * If the {@code value} parameter is {@code null}, it indicates that the corresponding entry should be deleted
     * from the meta.
     * </p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * Meta meta = new SomeMeta();
     * String key = "exampleKey";
     * SomeObject value = new SomeObject();
     *
     * // Set an object value in the meta associated with the specified key
     * meta.setObject(key, value);
     * }
     * </pre>
     *
     * @param key   The key to identify the value within the meta.
     * @param value The object to be saved, or {@code null} to delete the corresponding entry from the meta.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @throws BackendException     if an error occurs during the backend operation.
     * @see #setObject(String, String, Object)
     * @see #setObject(String, Object)
     */
    <T> void setObject(@NotNull String key, @Nullable T value) throws BackendException;

    /**
     * Creates and returns a root query update with the specified query update directives.
     *
     * <p>The {@code update} method returns a {@link QueryUpdate.RootQueryUpdate} that can be further configured with the
     * provided {@link QueryUpdateDirective} instances. It allows updating the underlying data store based on the specified directives.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * QueryUpdateDirective directive1 = new SomeQueryUpdateDirective();
     * QueryUpdateDirective directive2 = new AnotherQueryUpdateDirective();
     *
     * QueryUpdate.RootQueryUpdate rootQueryUpdate = someQueryManager.update(directive1, directive2);
     *
     * // Configure and execute the update operation
     * // (Additional directives or configuration can be applied to rootQueryUpdate)
     * rootQueryUpdate.execute();
     * }
     * </pre>
     *
     * @param queryUpdateDirective the query update directives to be applied to the root query update
     * @return a {@link QueryUpdate.RootQueryUpdate} configured with the specified directives
     */
    @NotNull
    QueryUpdate.RootQueryUpdate update(@NotNull QueryUpdateDirective... queryUpdateDirective);
}
