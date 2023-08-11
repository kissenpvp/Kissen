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

package net.kissenpvp.core.api.database.meta;

import net.kissenpvp.core.api.database.DataImplementation;
import net.kissenpvp.core.api.database.queryapi.QuerySelect;
import net.kissenpvp.core.api.database.queryapi.QueryUpdate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

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
     * Sets a value in the meta associated with the specified {@code totalID}.
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * If the {@code value} parameter is {@code null}, it indicates that the corresponding entry should be deleted
     * from the meta.
     * Implementing classes should handle the necessary logic to ensure the appropriate behavior based on the given
     * parameters.
     * </p>
     *
     * @param totalID The ID, which is located under the IDIdentifier.
     * @param key     Under MySQL, it refers to the column, and under files, it serves as the suffix used to
     *                recognize the value.
     * @param value   The value to be saved, or {@code null} to delete the corresponding entry from the meta.
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     * @see #setStringList(String, String, List)
     * @see #setRecordList(String, String, List)
     * @see #setRecord(String, String, Record)
     */
    void setString(@NotNull String totalID, @NotNull String key, @Nullable String value) throws BackendException;

    /**
     * Sets a value in the meta associated with the specified {@code key}.
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * If the {@code value} parameter is {@code null}, it indicates that the corresponding entry should be deleted
     * from the meta.
     * Implementing classes should handle the necessary logic to ensure the appropriate behavior based on the given
     * parameters.
     * </p>
     *
     * @param key   The key to identify the value within the meta.
     * @param value The value to be saved, or {@code null} to delete the corresponding entry from the meta.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #setString(String, String, String)
     */
    void setString(@NotNull String key, @Nullable String value) throws BackendException;

    /**
     * Sets a long value in the meta associated with the specified {@code totalID}.
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the long value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * Implementing classes should handle the necessary logic to ensure the appropriate behavior based on the given
     * parameters.
     * </p>
     *
     * @param totalID The ID, which is located under the IDIdentifier.
     * @param key     Under MySQL, it refers to the column, and under files, it serves as the suffix used to
     *                recognize the value.
     * @param value   The long value to be saved.
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     */
    void setLong(@NotNull String totalID, @NotNull String key, long value) throws BackendException;

    /**
     * Sets a long value in the meta associated with the specified {@code key}.
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the long value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * Implementing classes should handle the necessary logic to ensure the appropriate behavior based on the given
     * parameters.
     * </p>
     *
     * @param key   Under MySQL, it refers to the column, and under files, it serves as the suffix used to recognize
     *              the value.
     * @param value The long value to be saved.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #setLong(String, String, long)
     */
    void setLong(@NotNull String key, long value) throws BackendException;

    /**
     * Sets a double value in the meta associated with the specified {@code totalID}.
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the double value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * Implementing classes should handle the necessary logic to ensure the appropriate behavior based on the given
     * parameters.
     * </p>
     *
     * @param totalID The ID, which is located under the IDIdentifier.
     * @param key     Under MySQL, it refers to the column, and under files, it serves as the suffix used to
     *                recognize the value.
     * @param value   The double value to be saved (converted to string).
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     */
    void setDouble(@NotNull String totalID, @NotNull String key, double value) throws BackendException;

    /**
     * Sets a double value in the meta associated with the specified {@code key}.
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the double value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * Implementing classes should handle the necessary logic to ensure the appropriate behavior based on the given
     * parameters.
     * </p>
     *
     * @param key   Under MySQL, it refers to the column, and under files, it serves as the suffix used to recognize
     *              the value.
     * @param value The double value to be saved (converted to string).
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #setDouble(String, String, double)
     */
    void setDouble(@NotNull String key, double value) throws BackendException;

    /**
     * Sets a float value in the meta associated with the specified {@code totalID}.
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the float value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * Implementing classes should handle the necessary logic to ensure the appropriate behavior based on the given
     * parameters.
     * </p>
     *
     * @param totalID The ID, which is located under the IDIdentifier.
     * @param key     Under MySQL, it refers to the column, and under files, it serves as the suffix used to
     *                recognize the value.
     * @param value   The float value to be saved (converted to string).
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     */
    void setFloat(@NotNull String totalID, @NotNull String key, float value) throws BackendException;

    /**
     * Sets a float value in the meta associated with the specified {@code key}.
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the float value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * Implementing classes should handle the necessary logic to ensure the appropriate behavior based on the given
     * parameters.
     * </p>
     *
     * @param key   Under MySQL, it refers to the column, and under files, it serves as the suffix used to recognize
     *              the value.
     * @param value The float value to be saved (converted to string).
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #setFloat(String, String, float)
     */
    void setFloat(@NotNull String key, float value) throws BackendException;

    /**
     * Sets an integer value in the meta associated with the specified {@code totalID}.
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the integer value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * Implementing classes should handle the necessary logic to ensure the appropriate behavior based on the
     * given parameters.
     * </p>
     *
     * @param totalID The ID, which is located under the IDIdentifier.
     * @param key     Under MySQL, it refers to the column, and under files, it serves as the suffix used to
     *                recognize the value.
     * @param value   The integer value to be saved (converted to string).
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     */
    void setInt(@NotNull String totalID, @NotNull String key, int value) throws BackendException;

    /**
     * Sets an integer value in the meta associated with the specified {@code key}.
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the integer value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * Implementing classes should handle the necessary logic to ensure the appropriate behavior based on the given
     * parameters.
     * </p>
     *
     * @param key   Under MySQL, it refers to the column, and under files, it serves as the suffix used to recognize
     *              the value.
     * @param value The integer value to be saved (converted to string).
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #setInt(String, String, int)
     */
    void setInt(@NotNull String key, int value) throws BackendException;

    /**
     * Sets a short value in the meta associated with the specified {@code totalID}.
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the short value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * Implementing classes should handle the necessary logic to ensure the appropriate behavior based on the given
     * parameters.
     * </p>
     *
     * @param totalID The ID, which is located under the IDIdentifier.
     * @param key     Under MySQL, it refers to the column, and under files, it serves as the suffix used to
     *                recognize the value.
     * @param value   The short value to be saved (converted to string).
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     */
    void setShort(@NotNull String totalID, @NotNull String key, short value) throws BackendException;

    /**
     * Sets a short value in the meta associated with the specified {@code key}.
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the short value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * Implementing classes should handle the necessary logic to ensure the appropriate behavior based on the given
     * parameters.
     * </p>
     *
     * @param key   Under MySQL, it refers to the column, and under files, it serves as the suffix used to recognize
     *              the value.
     * @param value The short value to be saved (converted to string).
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #setShort(String, String, short)
     */
    void setShort(@NotNull String key, short value) throws BackendException;

    /**
     * Sets a byte value in the meta associated with the specified {@code totalID}.
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the byte value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * Implementing classes should handle the necessary logic to ensure the appropriate behavior based on the given
     * parameters.
     * </p>
     *
     * @param totalID The ID, which is located under the IDIdentifier.
     * @param key     Under MySQL, it refers to the column, and under files, it serves as the suffix used to
     *                recognize the value.
     * @param value   The byte value to be saved (converted to string).
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     */
    void setByte(@NotNull String totalID, @NotNull String key, byte value) throws BackendException;

    /**
     * Sets a byte value in the meta associated with the specified {@code key}.
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the byte value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * Implementing classes should handle the necessary logic to ensure the appropriate behavior based on the given
     * parameters.
     * </p>
     *
     * @param key   Under MySQL, it refers to the column, and under files, it serves as the suffix used to recognize
     *              the value.
     * @param value The byte value to be saved (converted to string).
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #setByte(String, String, byte)
     */
    void setByte(@NotNull String key, byte value) throws BackendException;

    /**
     * Sets a boolean value in the meta associated with the specified {@code totalID}.
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the boolean value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * Implementing classes should handle the necessary logic to ensure the appropriate behavior based on the given
     * parameters.
     * </p>
     *
     * @param totalID The ID, which is located under the IDIdentifier.
     * @param key     Under MySQL, it refers to the column, and under files, it serves as the suffix used to
     *                recognize the value.
     * @param value   The boolean value to be saved (converted to string).
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     */
    void setBoolean(@NotNull String totalID, @NotNull String key, boolean value) throws BackendException;

    /**
     * Sets a boolean value in the meta associated with the specified {@code key}.
     * The {@code key} parameter is used to identify the specific value within the meta.
     * The {@code value} parameter represents the boolean value to be saved.
     *
     * <p>
     * This operation may involve updating an existing value or adding a new key-value pair to the meta.
     * Implementing classes should handle the necessary logic to ensure the appropriate behavior based on the given
     * parameters.
     * </p>
     *
     * @param key   Under MySQL, it refers to the column, and under files, it serves as the suffix used to recognize
     *              the value.
     * @param value The boolean value to be saved (converted to string).
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #setBoolean(String, String, boolean)
     */
    void setBoolean(@NotNull String key, boolean value) throws BackendException;

    /**
     * Sets a list of string values in the meta associated with the specified {@code totalID} and {@code key}.
     * The {@code key} parameter is used to identify the specific list within the meta,
     * and the {@code value} parameter represents the list of string values to be saved.
     *
     * <p>
     * If the {@code value} parameter is {@code null}, the entry with the specified {@code totalID} and {@code key}
     * will be deleted from the meta.
     * </p>
     *
     * @param totalID The ID, which is located under the IDIdentifier.
     * @param key     Under MySQL, it refers to the column, and under files, it serves as the suffix used to
     *                recognize the value.
     * @param value   The list of string values to be saved, or {@code null} to delete the entry.
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     * @see #setStringList(String, List)
     * @see MetaReader#getStringList(String, String)
     * @see MetaReader#getStringList(String)
     */
    void setStringList(@NotNull String totalID, @NotNull String key, @Nullable List<String> value) throws BackendException;

    /**
     * Sets a list of string values in the meta associated with the specified {@code key}.
     * The {@code key} parameter is used to identify the specific list within the meta,
     * and the {@code value} parameter represents the list of string values to be saved.
     *
     * <p>
     * If the {@code value} parameter is {@code null}, the entry with the specified {@code key} will be deleted from
     * the meta.
     * </p>
     *
     * @param key   Under MySQL, it refers to the column, and under files, it serves as the suffix used to recognize
     *              the value.
     * @param value The list of string values to be saved, or {@code null} to delete the entry.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @throws BackendException     if a database access error occurs.
     * @see #setStringList(String, String, List)
     * @see MetaReader#getStringList(String, String)
     * @see MetaReader#getStringList(String)
     */
    void setStringList(@NotNull String key, @Nullable List<String> value) throws BackendException;

    /**
     * Sets a list of record objects in the meta associated with the specified {@code totalID} and {@code key}.
     * The {@code key} parameter is used to identify the specific list within the meta,
     * and the {@code value} parameter represents the list of record objects to be saved.
     *
     * <p>
     * If the {@code value} parameter is {@code null}, the entry with the specified {@code totalID} and {@code key}
     * will be deleted from the meta.
     * </p>
     *
     * <p>
     * <strong>Note:</strong> The record objects in the list will be serialized to JSON strings before being saved in
     * the meta.
     * The serialization is performed using the {@link DataImplementation#toJson(Record)} method.
     * </p>
     *
     * @param totalID The ID, which is located under the IDIdentifier.
     * @param key     Under MySQL, it refers to the column, and under files, it serves as the suffix used to
     *                recognize the value.
     * @param value   The list of record objects to be saved, or {@code null} to delete the entry.
     * @throws NullPointerException if {@code totalID}, {@code key}, or {@code value} is {@code null}.
     * @see #setString(String, String, String)
     * @see #setString(String, String)
     * @see #setRecordList(String, List)
     * @see #setRecord(String, String, Record)
     * @see #setRecord(String, Record)
     * @see MetaReader#getRecordList(String, String, Class)
     * @see MetaReader#getRecordList(String, Class)
     * @see MetaReader#getRecord(String, String, Class)
     * @see MetaReader#getRecord(String, Class)
     * @see DataImplementation#toJson(Record)
     */
    void setRecordList(@NotNull String totalID, @NotNull String key, @NotNull List<? extends Record> value) throws BackendException;

    /**
     * Sets a list of record objects in the meta associated with the specified {@code key}.
     * The {@code key} parameter is used to identify the specific list within the meta,
     * and the {@code value} parameter represents the list of record objects to be saved.
     *
     * <p>
     * If the {@code value} parameter is {@code null}, the entry with the specified {@code key} will be deleted from
     * the meta.
     * </p>
     *
     * <p>
     * Note The record objects in the list will be serialized to JSON strings before being saved in the meta.
     * The serialization is performed using the {@link DataImplementation#toJson(Record)} method.
     * </p>
     *
     * @param key   Under MySQL, it refers to the column, and under files, it serves as the suffix used to recognize
     *              the value.
     * @param value The list of record objects to be saved, or {@code null} to delete the entry.
     * @throws NullPointerException if {@code key} or {@code value} is {@code null}.
     * @throws BackendException     if a database access error occurs.
     * @see #setString(String, String, String)
     * @see #setString(String, String)
     * @see #setRecord(String, String, Record)
     * @see #setRecord(String, Record)
     * @see MetaReader#getRecordList(String, String, Class)
     * @see MetaReader#getRecordList(String, Class)
     * @see MetaReader#getRecord(String, String, Class)
     * @see MetaReader#getRecord(String, Class)
     * @see DataImplementation#toJson(Record)
     */
    void setRecordList(@NotNull String key, @NotNull List<? extends Record> value) throws BackendException;

    /**
     * Sets a value of type {@link Record} in the meta associated with the specified {@code totalID} and {@code key}.
     *
     * <p>
     * This method sets a value of type {@link Record} in the meta using the provided {@code totalID} and {@code key}
     * as identifiers.
     * The {@code value} parameter represents the value to be saved in the meta.
     * </p>
     *
     * <p>
     * Note: The {@code value} parameter must be an instance of a class that extends {@link Record}.
     * It is the responsibility of the caller to ensure that the value provided is of the correct type.
     * </p>
     *
     * <p>
     * Note: The value is converted to a JSON string using the {@link DataImplementation#toJson(Record)} (Object)
     * } method before saving it in the meta.
     * </p>
     *
     * @param totalID The ID, which is located under the IDIdentifier.
     * @param key     Under MySQL, it refers to the column, and under files, it serves as the suffix used to
     *                recognize the value.
     * @param value   The value of type {@link Record} to be saved in the meta.
     * @throws NullPointerException     if {@code totalID}, {@code key}, or {@code value} is {@code null}.
     * @throws IllegalArgumentException if the {@code value} is not an instance of a class that extends {@link Record}.
     * @see #setString(String, String, String)
     * @see #setString(String, String)
     * @see #setRecordList(String, String, List)
     * @see #setRecordList(String, List)
     * @see #setRecord(String, Record)
     * @see MetaReader#getRecordList(String, String, Class)
     * @see MetaReader#getRecordList(String, Class)
     * @see MetaReader#getRecord(String, String, Class)
     * @see MetaReader#getRecord(String, Class)
     * @see DataImplementation#toJson(Record)
     */
    <T extends Record> void setRecord(@NotNull String totalID, @NotNull String key, @NotNull T value) throws BackendException;

    /**
     * Sets a value of type {@link Record} in the meta associated with the specified {@code key}.
     *
     * <p>
     * This method sets a value of type {@link Record} in the meta using the provided {@code key} as the identifier.
     * The {@code value} parameter represents the value to be saved in the meta.
     * </p>
     *
     * <p>
     * Note: The {@code value} parameter must be an instance of a class that extends {@link Record}.
     * It is the responsibility of the caller to ensure that the value provided is of the correct type.
     * </p>
     *
     * <p>
     * Note: The value is converted to a JSON string using the {@link DataImplementation#toJson(Record)} method
     * before saving it in the meta.
     * </p>
     *
     * @param key   Under MySQL, it refers to the column, and under files, it serves as the suffix used to recognize
     *              the value.
     * @param value The value of type {@link Record} to be saved in the meta.
     * @throws NullPointerException     if {@code key} or {@code value} is {@code null}.
     * @throws IllegalArgumentException if the {@code value} is not an instance of a class that extends {@link Record}.
     * @throws BackendException         if a database access error occurs.
     * @see #setString(String, String, String)
     * @see #setString(String, String)
     * @see #setRecordList(String, String, List)
     * @see #setRecordList(String, List)
     * @see #setRecord(String, String, Record)
     * @see MetaReader#getRecordList(String, String, Class)
     * @see MetaReader#getRecordList(String, Class)
     * @see MetaReader#getRecord(String, String, Class)
     * @see MetaReader#getRecord(String, Class)
     * @see DataImplementation#toJson(Record)
     */
    <T extends Record> void setRecord(@NotNull String key, @NotNull T value) throws BackendException;

    @NotNull String[][] execute(@NotNull QuerySelect querySelect) throws BackendException;

    long execute(@NotNull QueryUpdate queryUpdate) throws BackendException;

}
