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
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.QuerySelect;
import net.kissenpvp.core.api.database.queryapi.QueryUpdate;
import net.kissenpvp.core.api.database.queryapi.QueryUpdateDirective;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * The MetaReader interface represents a reader for retrieving meta values from a database.
 * It extends the Serializable interface, indicating that implementations of this interface can be serialized.
 *
 * <p>
 * The MetaReader interface provides methods for retrieving various types of values from the meta in the table.
 * The methods operate synchronously and may consume system resources while waiting for the database retrieval
 * process to complete.
 * The execution time of these methods may vary depending on factors such as the size of the meta and the performance
 * characteristics of the underlying database system.
 * It is important to consider the potential resource implications and ensure proper resource management when calling
 * these methods.
 * </p>
 *
 * <p>
 * Note: All the methods in this interface may throw a BackendException if there is an error during the database
 * retrieval process.
 * It is important to handle this exception appropriately when using the MetaReader interface.
 * </p>
 *
 * @see Serializable
 * @see MetaWriter
 */
public interface MetaReader extends Serializable {
    /**
     * Pulls a previously saved meta value from the table and returns it.
     *
     * <p>
     * This method retrieves the value associated with the specified {@code totalID} and {@code key}
     * from the meta in the table. If a value is found, it is returned as an {@link Optional}.
     * If no value is found, the {@link Optional} will be empty.
     * </p>
     *
     * <p>
     * Note: This method operates synchronously and may consume system resources while waiting for the database
     * retrieval process to complete.
     * Depending on factors such as the size of the meta and the performance characteristics of the underlying
     * database system, this method's execution time may vary.
     * Consider the potential resource implications when calling this method and ensure proper resource management.
     * </p>
     *
     * @param totalID The ID, which is located under the IDIdentifier.
     * @param key     Under MySQL, it refers to the column, and under files, it serves as the suffix used to
     *                recognize the value.
     * @return The value that was found if present, or an empty {@link Optional} if no value was found.
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     */
    @NotNull Optional<@Nullable String> getString(@NotNull String totalID, @NotNull String key) throws BackendException;

    /**
     * Retrieves a previously saved string value associated with the specified {@code key}.
     *
     * <p>
     * This method retrieves the string value associated with the specified {@code key} from the meta in the table.
     * If a value is found, it is returned as an {@link Optional}. If no value is found, the
     * {@link Optional} will be empty.
     * </p>
     *
     * <p>
     * Note: This method operates synchronously and may consume system resources while waiting for the database
     * retrieval process to complete.
     * Depending on factors such as the size of the meta and the performance characteristics of the underlying
     * database system,
     * this method's execution time may vary. Consider the potential resource implications when calling this
     * method and ensure proper resource management.
     * </p>
     *
     * @param key The key referring to the column under MySQL, or serving as the suffix used to recognize the value
     *            under files.
     * @return The string value that was found if present, or an empty {@link Optional} if no value was found.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see MetaWriter#setString(String, String, String)
     */
    @NotNull Optional<@Nullable String> getString(@NotNull String key) throws BackendException;

    /**
     * Retrieves a previously saved long value associated with the specified {@code totalID} and {@code key}.
     *
     * <p>
     * This method retrieves the long value associated with the specified {@code totalID} and {@code key} from
     * the meta in the table.
     * If a value is found, it is returned as an {@link Optional}. If no value is found, the
     * {@link Optional} will be empty.
     * </p>
     *
     * <p>
     * Note: This method operates synchronously and may consume system resources while waiting for the database
     * retrieval process to complete.
     * Depending on factors such as the size of the meta and the performance characteristics of the underlying
     * database system,
     * this method's execution time may vary. Consider the potential resource implications when calling this
     * method and ensure proper resource management.
     * </p>
     *
     * @param totalID The ID located under the IDIdentifier.
     * @param key     The key referring to the column under MySQL, or serving as the suffix used to recognize the
     *                value under files.
     * @return The long value that was found if present, or an empty {@link Optional} if no value was found.
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     */
    @NotNull Optional<@Nullable Long> getLong(@NotNull String totalID, @NotNull String key) throws BackendException;

    /**
     * Retrieves a previously saved long value associated with the specified {@code key}.
     *
     * <p>
     * This method retrieves the long value associated with the specified {@code key} from the meta in the table.
     * If a value is found, it is returned as an {@link Optional}. If no value is found, the
     * {@link Optional} will be empty.
     * </p>
     *
     * <p>
     * Note: This method operates synchronously and may consume system resources while waiting for the database
     * retrieval process to complete.
     * Depending on factors such as the size of the meta and the performance characteristics of the underlying
     * database system,
     * this method's execution time may vary. Consider the potential resource implications when calling this method
     * and ensure proper resource management.
     * </p>
     *
     * @param key The key referring to the column under MySQL, or serving as the suffix used to recognize the value
     *            under files.
     * @return The long value that was found if present, or an empty {@link Optional} if no value was found.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getLong(String, String)
     */
    @NotNull Optional<@Nullable Long> getLong(@NotNull String key) throws BackendException;

    /**
     * Retrieves a previously saved double value associated with the specified {@code totalID} and {@code key}.
     *
     * <p>
     * This method retrieves the double value associated with the specified {@code totalID} and {@code key} from the
     * meta in the table.
     * If a value is found, it is returned as an {@link Optional}. If no value is found, the
     * {@link Optional} will be empty.
     * </p>
     *
     * <p>
     * Note: This method operates synchronously and may consume system resources while waiting for the database
     * retrieval process to complete.
     * Depending on factors such as the size of the meta and the performance characteristics of the underlying
     * database system,
     * this method's execution time may vary. Consider the potential resource implications when calling this method
     * and ensure proper resource management.
     * </p>
     *
     * @param totalID The ID located under the IDIdentifier.
     * @param key     The key referring to the column under MySQL, or serving as the suffix used to recognize the
     *                value under files.
     * @return The double value that was found if present, or an empty {@link Optional} if no value was found.
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     */
    @NotNull Optional<@Nullable Double> getDouble(@NotNull String totalID, @NotNull String key) throws BackendException;

    /**
     * Retrieves a previously saved double value associated with the specified {@code key}.
     *
     * <p>
     * This method retrieves the double value associated with the specified {@code key} from the meta in the table.
     * If a value is found, it is returned as an {@link Optional}. If no value is found, the
     * {@link Optional} will be empty.
     * </p>
     *
     * <p>
     * Note: This method operates synchronously and may consume system resources while waiting for the database
     * retrieval process to complete.
     * Depending on factors such as the size of the meta and the performance characteristics of the underlying
     * database system,
     * this method's execution time may vary. Consider the potential resource implications when calling this method
     * and ensure proper resource management.
     * </p>
     *
     * @param key The key referring to the column under MySQL, or serving as the suffix used to recognize the value
     *            under files.
     * @return The double value that was found if present, or an empty {@link Optional} if no value was found.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getDouble(String, String)
     */
    @NotNull Optional<@Nullable Double> getDouble(@NotNull String key) throws BackendException;

    /**
     * Retrieves a previously saved float value associated with the specified {@code totalID} and {@code key}.
     *
     * <p>
     * This method retrieves the float value associated with the specified {@code totalID} and {@code key} from the
     * meta in the table.
     * If a value is found, it is returned as an {@link Optional}. If no value is found, the
     * {@link Optional} will be empty.
     * </p>
     *
     * <p>
     * Note: This method operates synchronously and may consume system resources while waiting for the database
     * retrieval process to complete.
     * Depending on factors such as the size of the meta and the performance characteristics of the underlying
     * database system,
     * this method's execution time may vary. Consider the potential resource implications when calling this method
     * and ensure proper resource management.
     * </p>
     *
     * @param totalID The ID located under the IDIdentifier.
     * @param key     The key referring to the column under MySQL, or serving as the suffix used to recognize the
     *                value under files.
     * @return The float value that was found if present, or an empty {@link Optional} if no value was found.
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     */
    @NotNull Optional<@Nullable Float> getFloat(@NotNull String totalID, @NotNull String key) throws BackendException;

    /**
     * Retrieves a previously saved float value associated with the specified {@code key}.
     *
     * <p>
     * This method retrieves the float value associated with the specified {@code key} from the meta in the table.
     * If a value is found, it is returned as an {@link Optional}. If no value is found, the
     * {@link Optional} will be empty.
     * </p>
     *
     * <p>
     * Note: This method operates synchronously and may consume system resources while waiting for the database
     * retrieval process to complete.
     * Depending on factors such as the size of the meta and the performance characteristics of the underlying
     * database system,
     * this method's execution time may vary. Consider the potential resource implications when calling this method
     * and ensure proper resource management.
     * </p>
     *
     * @param key The key referring to the column under MySQL, or serving as the suffix used to recognize the value
     *            under files.
     * @return The float value that was found if present, or an empty {@link Optional} if no value was found.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getFloat(String, String)
     */
    @NotNull Optional<@Nullable Float> getFloat(@NotNull String key) throws BackendException;

    /**
     * Retrieves a previously saved integer value associated with the specified {@code totalID} and {@code key}.
     *
     * <p>
     * This method retrieves the integer value associated with the specified {@code totalID} and {@code key} from the
     * meta in the table.
     * If a value is found, it is returned as an {@link Optional}. If no value is found, the
     * {@link Optional} will be empty.
     * </p>
     *
     * <p>
     * Note: This method operates synchronously and may consume system resources while waiting for the database
     * retrieval process to complete.
     * Depending on factors such as the size of the meta and the performance characteristics of the underlying
     * database system,
     * this method's execution time may vary. Consider the potential resource implications when calling this method
     * and ensure proper resource management.
     * </p>
     *
     * @param totalID The ID located under the IDIdentifier.
     * @param key     The key referring to the column under MySQL, or serving as the suffix used to recognize the
     *                value under files.
     * @return The integer value that was found if present, or an empty {@link Optional} if no value was
     * found.
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     */
    @NotNull Optional<@Nullable Integer> getInt(@NotNull String totalID, @NotNull String key) throws BackendException;

    /**
     * Retrieves a previously saved integer value associated with the specified {@code key}.
     *
     * <p>
     * This method retrieves the integer value associated with the specified {@code key} from the meta in the table.
     * If a value is found, it is returned as an {@link Optional}. If no value is found, the
     * {@link Optional} will be empty.
     * </p>
     *
     * <p>
     * Note: This method operates synchronously and may consume system resources while waiting for the database
     * retrieval process to complete.
     * Depending on factors such as the size of the meta and the performance characteristics of the underlying
     * database system,
     * this method's execution time may vary. Consider the potential resource implications when calling this method
     * and ensure proper resource management.
     * </p>
     *
     * @param key The key referring to the column under MySQL, or serving as the suffix used to recognize the value
     *            under files.
     * @return The integer value that was found if present, or an empty {@link Optional} if no value was
     * found.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getInt(String, String)
     */
    @NotNull Optional<@Nullable Integer> getInt(@NotNull String key) throws BackendException;

    /**
     * Retrieves a previously saved short value associated with the specified {@code totalID} and {@code key}.
     *
     * <p>
     * This method retrieves the short value associated with the specified {@code totalID} and {@code key} from the
     * meta in the table.
     * If a value is found, it is returned as an {@link Optional}. If no value is found, the
     * {@link Optional} will be empty.
     * </p>
     *
     * <p>
     * Note: This method operates synchronously and may consume system resources while waiting for the database
     * retrieval process to complete.
     * Depending on factors such as the size of the meta and the performance characteristics of the underlying
     * database system,
     * this method's execution time may vary. Consider the potential resource implications when calling this method
     * and ensure proper resource management.
     * </p>
     *
     * @param totalID The ID located under the IDIdentifier.
     * @param key     The key referring to the column under MySQL, or serving as the suffix used to recognize the
     *                value under files.
     * @return The short value that was found if present, or an empty {@link Optional} if no value was found.
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     */
    @NotNull Optional<@Nullable Short> getShort(@NotNull String totalID, @NotNull String key) throws BackendException;

    /**
     * Retrieves a previously saved short value associated with the specified {@code key}.
     *
     * <p>
     * This method retrieves the short value associated with the specified {@code key} from the meta in the table.
     * If a value is found, it is returned as an {@link Optional}. If no value is found, the
     * {@link Optional} will be empty.
     * </p>
     *
     * <p>
     * Note: This method operates synchronously and may consume system resources while waiting for the database
     * retrieval process to complete.
     * Depending on factors such as the size of the meta and the performance characteristics of the underlying
     * database system,
     * this method's execution time may vary. Consider the potential resource implications when calling this method
     * and ensure proper resource management.
     * </p>
     *
     * @param key The key referring to the column under MySQL, or serving as the suffix used to recognize the value
     *            under files.
     * @return The short value that was found if present, or an empty {@link Optional} if no value was found.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getShort(String, String)
     */
    @NotNull Optional<@Nullable Short> getShort(@NotNull String key) throws BackendException;

    /**
     * Retrieves a previously saved byte value associated with the specified {@code totalID} and {@code key}.
     *
     * <p>
     * This method retrieves the byte value associated with the specified {@code totalID} and {@code key} from the
     * meta in the table.
     * If a value is found, it is returned as an {@link Optional}. If no value is found, the
     * {@link Optional} will be empty.
     * </p>
     *
     * <p>
     * Note: This method operates synchronously and may consume system resources while waiting for the database
     * retrieval process to complete.
     * Depending on factors such as the size of the meta and the performance characteristics of the underlying
     * database system,
     * this method's execution time may vary. Consider the potential resource implications when calling this method
     * and ensure proper resource management.
     * </p>
     *
     * @param totalID The ID located under the IDIdentifier.
     * @param key     The key referring to the column under MySQL, or serving as the suffix used to recognize the
     *                value under files.
     * @return The byte value that was found if present, or an empty {@link Optional} if no value was found.
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     */
    @NotNull Optional<@Nullable Byte> getByte(@NotNull String totalID, @NotNull String key) throws BackendException;

    /**
     * Retrieves a previously saved byte value associated with the specified {@code key}.
     *
     * <p>
     * This method retrieves the byte value associated with the specified {@code key} from the meta in the table.
     * If a value is found, it is returned as an {@link Optional}. If no value is found, the
     * {@link Optional} will be empty.
     * </p>
     *
     * <p>
     * Note: This method operates synchronously and may consume system resources while waiting for the database
     * retrieval process to complete.
     * Depending on factors such as the size of the meta and the performance characteristics of the underlying
     * database system,
     * this method's execution time may vary. Consider the potential resource implications when calling this method
     * and ensure proper resource management.
     * </p>
     *
     * @param key The key referring to the column under MySQL, or serving as the suffix used to recognize the value
     *            under files.
     * @return The byte value that was found if present, or an empty {@link Optional} if no value was found.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getByte(String, String)
     */
    @NotNull Optional<@Nullable Byte> getByte(@NotNull String key) throws BackendException;

    /**
     * Retrieves a previously saved boolean value associated with the specified {@code totalID} and {@code key}.
     *
     * <p>
     * This method retrieves the boolean value associated with the specified {@code totalID} and {@code key} from the
     * meta in the table.
     * If a value is found, it is returned as an {@link Optional}. If no value is found, the
     * {@link Optional} will be empty.
     * </p>
     *
     * <p>
     * Note: This method operates synchronously and may consume system resources while waiting for the database
     * retrieval process to complete.
     * Depending on factors such as the size of the meta and the performance characteristics of the underlying
     * database system,
     * this method's execution time may vary. Consider the potential resource implications when calling this method
     * and ensure proper resource management.
     * </p>
     *
     * @param totalID The ID located under the IDIdentifier.
     * @param key     The key referring to the column under MySQL, or serving as the suffix used to recognize the
     *                value under files.
     * @return The boolean value that was found if present, or an empty {@link Optional} if no value was
     * found.
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     */
    @NotNull Optional<@Nullable Boolean> getBoolean(@NotNull String totalID, @NotNull String key) throws BackendException;

    /**
     * Retrieves a previously saved boolean value associated with the specified {@code key}.
     *
     * <p>
     * This method retrieves the boolean value associated with the specified {@code key} from the meta in the table.
     * If a value is found, it is returned as an {@link Optional}. If no value is found, the
     * {@link Optional} will be empty.
     * </p>
     *
     * <p>
     * Note: This method operates synchronously and may consume system resources while waiting for the database
     * retrieval process to complete.
     * Depending on factors such as the size of the meta and the performance characteristics of the underlying
     * database system,
     * this method's execution time may vary. Consider the potential resource implications when calling this method
     * and ensure proper resource management.
     * </p>
     *
     * @param key The key referring to the column under MySQL, or serving as the suffix used to recognize the value
     *            under files.
     * @return The boolean value that was found if present, or an empty {@link Optional} if no value was
     * found.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getBoolean(String, String)
     */
    @NotNull Optional<@Nullable Boolean> getBoolean(@NotNull String key) throws BackendException;

    /**
     * Retrieves a previously saved meta list from the table and returns it.
     *
     * <p>
     * This method retrieves a list of previously saved values associated with the specified {@code totalID} and
     * {@code key}
     * from the meta in the table. The retrieved values are returned as an unmodifiable {@link List} of
     * strings.
     * If no values are found, an empty list of strings will be returned.
     * </p>
     *
     * <p>
     * Note: The behavior and resource implications of this method can vary depending on the implementation.
     * Implementations should ensure that the returned list is unmodifiable to maintain immutability and consistency.
     * </p>
     *
     * @param totalID The ID, which is located under the IDIdentifier.
     * @param key     Under MySQL, it refers to the column, and under files, it serves as the suffix used to
     *                recognize the value.
     * @return The values that were found if any, or an empty unmodifiable list of strings if no values were found.
     * @throws NullPointerException if {@code totalID} or {@code key} is {@code null}.
     * @see MetaWriter#setStringList(String, String, List)
     * @see MetaWriter#setStringList(String, List)
     * @see #getStringList(String)
     */
    @NotNull @Unmodifiable List<String> getStringList(@NotNull String totalID, @NotNull String key) throws BackendException;

    /**
     * Retrieves a previously saved meta list from the table and returns it.
     *
     * <p>
     * This method retrieves a list of previously saved values associated with the specified {@code key}
     * from the meta in the table. The retrieved values are returned as an unmodifiable {@link List} of
     * strings.
     * If no values are found, an empty list of strings will be returned.
     * </p>
     *
     * <p>
     * Note: The behavior and resource implications of this method can vary depending on the implementation.
     * Implementations should ensure that the returned list is unmodifiable to maintain immutability and consistency.
     * </p>
     *
     * @param key Under MySQL, it refers to the column, and under files, it serves as the suffix used to recognize
     *            the value.
     * @return The values that were found if any, or an empty unmodifiable list of strings if no values were found.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @throws BackendException     if a database access error occurs.
     * @see MetaWriter#setStringList(String, String, List)
     * @see MetaWriter#setStringList(String, List)
     * @see #getStringList(String, String)
     */
    @NotNull @Unmodifiable List<String> getStringList(@NotNull String key) throws BackendException;

    /**
     * Retrieves a list of records of the specified type associated with the meta in the table.
     *
     * <p>
     * This method retrieves a list of records of the given type associated with the specified {@code totalID} and
     * {@code key}
     * from the meta in the table. The type of the records to retrieve is specified by the {@code record} parameter,
     * which should be a subclass of {@link Record}.
     * The retrieved records are returned as an unmodifiable {@link List}.
     * If no records are found, an empty list will be returned.
     * </p>
     *
     * <p>
     * Note: The {@link #getStringList(String, String)} method is used internally to retrieve the meta value as a
     * list of strings.
     * Depending on the implementation of {@link #getStringList(String, String)}, the behavior and resource
     * implications of that method apply here as well.
     * It is recommended to refer to the Javadoc of {@link #getStringList(String, String)} for more details on its
     * usage and considerations.
     * </p>
     *
     * @param totalID The ID, which is located under the IDIdentifier.
     * @param key     Under MySQL, it refers to the column, and under files, it serves as the suffix used to
     *                recognize the value.
     * @param record  The class of the {@link Record} to retrieve.
     * @param <T>     The type of record to return, which should be a subclass of {@link Record}.
     * @return A list of the given records if found, or an empty unmodifiable list if no records were found.
     * @throws NullPointerException if {@code totalID}, {@code key}, or {@code record} is {@code null}.
     * @see MetaWriter#setRecordList(String, String, List)
     * @see MetaWriter#setRecordList(String, List)
     * @see MetaWriter#setRecord(String, String, Record)
     * @see MetaWriter#setRecord(String, Record)
     * @see #getString(String, String)
     * @see #getString(String)
     * @see #getRecordList(String, Class)
     * @see #getRecord(String, String, Class)
     * @see #getRecord(String, Class)
     * @see DataImplementation#toJson(Record)
     */
    <T extends Record> @NotNull @Unmodifiable List<T> getRecordList(@NotNull String totalID, @NotNull String key,
                                                                    @NotNull Class<T> record) throws BackendException;

    /**
     * Retrieves a record of type {@link Record} from the meta associated with the specified {@code key}.
     *
     * <p>
     * This method retrieves a record of type {@link Record} from the meta using the provided {@code key} as the
     * identifier.
     * The {@code record} parameter specifies the class type of the record to be retrieved.
     * It internally utilizes the {@link #getString(String, String)} method to retrieve the JSON string
     * representation of the record from the meta.
     * The JSON string is then deserialized into an instance of the specified record class using the
     * {@link DataImplementation#fromJson(String, Class)} method.
     * </p>
     *
     * <p>
     * Note: Retrieving and deserializing the record can involve performance considerations depending on the size and
     * complexity of the record and the underlying database implementation.
     * It is important to be mindful of the potential impact on resource usage and response time when using this method.
     * </p>
     *
     * <p>
     * Note: The returned record is wrapped in an {@link Optional} to indicate the possibility of a null result.
     * If no record is found in the meta, an empty {@link Optional} will be returned.
     * </p>
     *
     * @param key    Under MySQL, it refers to the column, and under files, it serves as the suffix used to recognize
     *               the value.
     * @param record The class type of the record to be retrieved.
     * @param <T>    The type parameter representing the record type that extends {@link Record}.
     * @return An {@link Optional} containing the retrieved record, or an empty {@link Optional} if no record is found.
     * @throws NullPointerException if {@code key} or {@code record} is {@code null}.
     * @throws BackendException     if a database access error occurs.
     * @see MetaWriter#setRecordList(String, String, List)
     * @see MetaWriter#setRecordList(String, List)
     * @see MetaWriter#setRecord(String, String, Record)
     * @see MetaWriter#setRecord(String, Record)
     * @see #getString(String, String)
     * @see #getString(String)
     * @see #getRecordList(String, String, Class)
     * @see #getRecord(String, String, Class)
     * @see #getRecord(String, Class)
     * @see DataImplementation#toJson(Record)
     */
    <T extends Record> @NotNull @Unmodifiable List<T> getRecordList(@NotNull String key, @NotNull Class<T> record) throws BackendException;

    /**
     * Retrieves a record of type {@link Record} from the meta associated with the specified {@code totalID} and
     * {@code key}.
     *
     * <p>
     * This method retrieves a record of type {@link Record} from the meta using the provided {@code totalID} and
     * {@code key} as identifiers.
     * The {@code record} parameter specifies the class type of the record to be retrieved.
     * It internally utilizes the {@link #getString(String, String)} method to retrieve the JSON string
     * representation of the record from the meta.
     * The JSON string is then deserialized into an instance of the specified record class using the
     * {@link DataImplementation#fromJson(String, Class)} method.
     * </p>
     *
     * <p>
     * Note: Retrieving and deserializing the record can involve performance considerations depending on the size and
     * complexity of the record and the underlying database implementation.
     * It is important to be mindful of the potential impact on resource usage and response time when using this method.
     * </p>
     *
     * <p>
     * Note: The returned record is wrapped in an {@link Optional} to indicate the possibility of a null result.
     * If no record is found in the meta, an empty {@link Optional} will be returned.
     * </p>
     *
     * @param totalID The ID, which is located under the IDIdentifier.
     * @param key     Under MySQL, it refers to the column, and under files, it serves as the suffix used to
     *                recognize the value.
     * @param record  The class type of the record to be retrieved.
     * @param <T>     The type parameter representing the record type that extends {@link Record}.
     * @return An {@link Optional} containing the retrieved record, or an empty {@link Optional} if no record is found.
     * @throws NullPointerException if {@code totalID}, {@code key}, or {@code record} is {@code null}.
     * @see MetaWriter#setRecordList(String, String, List)
     * @see MetaWriter#setRecordList(String, List)
     * @see MetaWriter#setRecord(String, String, Record)
     * @see MetaWriter#setRecord(String, Record)
     * @see #getString(String, String)
     * @see #getString(String)
     * @see #getRecordList(String, String, Class)
     * @see #getRecordList(String, Class)
     * @see #getRecord(String, Class)
     * @see DataImplementation#toJson(Record)
     */
    <T extends Record> @NotNull Optional<@Nullable T> getRecord(@NotNull String totalID, @NotNull String key,
                                                                @NotNull Class<T> record) throws BackendException;

    /**
     * Retrieves a record of type {@link Record} from the meta associated with the specified {@code key}.
     *
     * <p>
     * This method retrieves a record of type {@link Record} from the meta using the provided {@code key} as the
     * identifier.
     * The {@code record} parameter specifies the class type of the record to be retrieved.
     * It internally utilizes the {@link #getString(String, String)} method to retrieve the JSON string
     * representation of the record from the meta.
     * The JSON string is then deserialized into an instance of the specified record class using the
     * {@link DataImplementation#fromJson(String, Class)} method.
     * </p>
     *
     * <p>
     * Note: Retrieving and deserializing the record can involve performance considerations depending on the size and
     * complexity of the record and the underlying database implementation.
     * It is important to be mindful of the potential impact on resource usage and response time when using this method.
     * </p>
     *
     * <p>
     * Note: The returned record is wrapped in an {@link Optional} to indicate the possibility of a null result.
     * If no record is found in the meta, an empty {@link Optional} will be returned.
     * </p>
     *
     * @param key    Under MySQL, it refers to the column, and under files, it serves as the suffix used to recognize
     *               the value.
     * @param record The class type of the record to be retrieved.
     * @param <T>    The type parameter representing the record type that extends {@link Record}.
     * @return An {@link Optional} containing the retrieved record, or an empty {@link Optional} if no record is found.
     * @throws NullPointerException if {@code key} or {@code record} is {@code null}.
     * @throws BackendException     if a database access error occurs.
     * @see MetaWriter#setRecordList(String, String, List)
     * @see MetaWriter#setRecordList(String, List)
     * @see MetaWriter#setRecord(String, String, Record)
     * @see MetaWriter#setRecord(String, Record)
     * @see #getString(String, String)
     * @see #getString(String)
     * @see #getRecordList(String, String, Class)
     * @see #getRecordList(String, Class)
     * @see #getRecord(String, String, Class)
     * @see DataImplementation#toJson(Record)
     */
    <T extends Record> @NotNull Optional<@Nullable T> getRecord(@NotNull String key, @NotNull Class<T> record) throws BackendException;

    @NotNull QueryUpdate update(@NotNull QueryUpdateDirective... queryUpdateDirective);

    @NotNull QuerySelect select(@NotNull Column... columns);
}
