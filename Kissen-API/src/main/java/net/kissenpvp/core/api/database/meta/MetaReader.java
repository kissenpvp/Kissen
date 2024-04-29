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

import net.kissenpvp.core.api.database.queryapi.*;
import net.kissenpvp.core.api.database.queryapi.select.QuerySelect;
import net.kissenpvp.core.api.database.meta.list.MetaList;
import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.api.database.savable.SavableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
     * Asynchronously retrieves a {@link String} value associated with the specified total ID and key.
     * <p>
     * The {@code getString} method returns a {@link CompletableFuture} that will be completed with the retrieved
     * {@link String} value when the operation is successful. If the operation encounters an error, the
     * {@link CompletableFuture} will be completed exceptionally with a {@link Exception}.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * String totalID = "exampleTotalID";
     * String key = "exampleKey";
     *
     * CompletableFuture<String> futureResult = someMeta.getString(totalID, key);
     *
     * // Asynchronously handle the result when it becomes available
     * futureResult.thenAccept(result -> System.out.println("Retrieved value: " + result))
     *             .exceptionally(ex -> {
     *                 System.err.println("Error retrieving value: " + ex.getMessage());
     *                 return null;
     *             });
     * }
     * </pre>
     *
     * @param totalID the total ID associated with the value to be retrieved
     * @param key     the key associated with the value to be retrieved
     * @return a {@link CompletableFuture} that will be completed with the retrieved {@link String} value,
     * or exceptionally with a {@link Exception} if an error occurs
     * @see #getString(String)
     * @see MetaWriter#setString(String, String, String)
     * @see MetaWriter#setString(String, String)
     */
    @NotNull
    CompletableFuture<String> getString(@NotNull String totalID, @NotNull String key);

    /**
     * Asynchronously retrieves a {@link String} value associated with the specified key.
     *
     * <p>The {@code getString} method returns a {@link CompletableFuture} that will be completed with the retrieved
     * {@link String} value when the operation is successful. If the operation encounters an error, the
     * {@link CompletableFuture} will be completed exceptionally with a {@link Exception}.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * String key = "exampleKey";
     *
     * CompletableFuture<String> futureResult = someMeta.getString(key);
     *
     * // Asynchronously handle the result when it becomes available
     * futureResult.thenAccept(result -> System.out.println("Retrieved value: " + result))
     *             .exceptionally(ex -> {
     *                 System.err.println("Error retrieving value: " + ex.getMessage());
     *                 return null;
     *             });
     * }
     * </pre>
     *
     * @param key the key associated with the value to be retrieved
     * @return a {@link CompletableFuture} that will be completed with the retrieved {@link String} value,
     * or exceptionally with a {@link Exception} if an error occurs
     * @see #getString(String, String)
     * @see MetaWriter#setString(String, String)
     * @see MetaWriter#setString(String, String, String)
     */
    @NotNull
    CompletableFuture<String> getString(@NotNull String key);

    /**
     * Asynchronously retrieves a {@link Long} value associated with the specified total ID and key.
     *
     * <p>The {@code getLong} method returns a {@link CompletableFuture} that will be completed with the retrieved
     * {@link Long} value when the operation is successful. If the operation encounters an error, the
     * {@link CompletableFuture} will be completed exceptionally with a {@link Exception}.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * String totalID = "exampleTotalID";
     * String key = "exampleKey";
     *
     * CompletableFuture<Long> futureResult = someMeta.getLong(totalID, key);
     *
     * // Asynchronously handle the result when it becomes available
     * futureResult.thenAccept(result -> System.out.println("Retrieved value: " + result))
     *             .exceptionally(ex -> {
     *                 System.err.println("Error retrieving value: " + ex.getMessage());
     *                 return null;
     *             });
     * }
     * </pre>
     *
     * @param totalID the total ID associated with the value to be retrieved
     * @param key     the key associated with the value to be retrieved
     * @return a {@link CompletableFuture} that will be completed with the retrieved {@link Long} value,
     * or exceptionally with a {@link Exception} if an error occurs
     * @see #getLong(String)
     * @see MetaWriter#setLong(String, long)
     * @see MetaWriter#setLong(String, String, long)
     */
    @NotNull
    CompletableFuture<Long> getLong(@NotNull String totalID, @NotNull String key);

    /**
     * Asynchronously retrieves a {@link Long} value associated with the specified key.
     *
     * <p>The {@code getLong} method returns a {@link CompletableFuture} that will be completed with the retrieved
     * {@link Long} value when the operation is successful. If the operation encounters an error, the
     * {@link CompletableFuture} will be completed exceptionally with a {@link Exception}.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * String key = "exampleKey";
     *
     * CompletableFuture<Long> futureResult = someMeta.getLong(key);
     *
     * // Asynchronously handle the result when it becomes available
     * futureResult.thenAccept(result -> System.out.println("Retrieved value: " + result))
     *             .exceptionally(ex -> {
     *                 System.err.println("Error retrieving value: " + ex.getMessage());
     *                 return null;
     *             });
     * }
     * </pre>
     *
     * @param key the key associated with the value to be retrieved
     * @return a {@link CompletableFuture} that will be completed with the retrieved {@link Long} value,
     * or exceptionally with a {@link Exception} if an error occurs
     * @see #getLong(String, String)
     * @see MetaWriter#setLong(String, long)
     * @see MetaWriter#setLong(String, String, long)
     */
    @NotNull
    CompletableFuture<Long> getLong(@NotNull String key);

    /**
     * Asynchronously retrieves a {@link Double} value associated with the specified total ID and key.
     *
     * <p>The {@code getDouble} method returns a {@link CompletableFuture} that will be completed with the retrieved
     * {@link Double} value when the operation is successful. If the operation encounters an error, the
     * {@link CompletableFuture} will be completed exceptionally with a {@link Exception}.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * String totalID = "exampleTotalID";
     * String key = "exampleKey";
     *
     * CompletableFuture<Double> futureResult = someMeta.getDouble(totalID, key);
     *
     * // Asynchronously handle the result when it becomes available
     * futureResult.thenAccept(result -> System.out.println("Retrieved value: " + result))
     *             .exceptionally(ex -> {
     *                 System.err.println("Error retrieving value: " + ex.getMessage());
     *                 return null;
     *             });
     * }
     * </pre>
     *
     * @param totalID the total ID associated with the value to be retrieved
     * @param key     the key associated with the value to be retrieved
     * @return a {@link CompletableFuture} that will be completed with the retrieved {@link Double} value,
     * or exceptionally with a {@link Exception} if an error occurs
     * @see #getDouble(String)
     * @see MetaWriter#setDouble(String, double)
     * @see MetaWriter#setDouble(String, String, double)
     */
    @NotNull
    CompletableFuture<Double> getDouble(@NotNull String totalID, @NotNull String key);

    /**
     * Asynchronously retrieves a {@link Double} value associated with the specified key.
     *
     * <p>The {@code getDouble} method returns a {@link CompletableFuture} that will be completed with the retrieved
     * {@link Double} value when the operation is successful. If the operation encounters an error, the
     * {@link CompletableFuture} will be completed exceptionally with a {@link Exception}.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * String key = "exampleKey";
     *
     * CompletableFuture<Double> futureResult = someMeta.getDouble(key);
     *
     * // Asynchronously handle the result when it becomes available
     * futureResult.thenAccept(result -> System.out.println("Retrieved value: " + result))
     *             .exceptionally(ex -> {
     *                 System.err.println("Error retrieving value: " + ex.getMessage());
     *                 return null;
     *             });
     * }
     * </pre>
     *
     * @param key the key associated with the value to be retrieved
     * @return a {@link CompletableFuture} that will be completed with the retrieved {@link Double} value,
     * or exceptionally with a {@link Exception} if an error occurs
     * @see #getDouble(String, String)
     * @see MetaWriter#setDouble(String, double)
     * @see MetaWriter#setDouble(String, String, double)
     */
    @NotNull
    CompletableFuture<Double> getDouble(@NotNull String key);

    /**
     * Asynchronously retrieves a {@link Float} value associated with the specified total ID and key.
     *
     * <p>The {@code getFloat} method returns a {@link CompletableFuture} that will be completed with the retrieved
     * {@link Float} value when the operation is successful. If the operation encounters an error, the
     * {@link CompletableFuture} will be completed exceptionally with a {@link Exception}.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * String totalID = "exampleTotalID";
     * String key = "exampleKey";
     *
     * CompletableFuture<Float> futureResult = someMeta.getFloat(totalID, key);
     *
     * // Asynchronously handle the result when it becomes available
     * futureResult.thenAccept(result -> System.out.println("Retrieved value: " + result))
     *             .exceptionally(ex -> {
     *                 System.err.println("Error retrieving value: " + ex.getMessage());
     *                 return null;
     *             });
     * }
     * </pre>
     *
     * @param totalID the total ID associated with the value to be retrieved
     * @param key     the key associated with the value to be retrieved
     * @return a {@link CompletableFuture} that will be completed with the retrieved {@link Float} value,
     * or exceptionally with a {@link Exception} if an error occurs
     * @see #getFloat(String)
     * @see MetaWriter#setFloat(String, float)
     * @see MetaWriter#setFloat(String, String, float)
     */
    @NotNull
    CompletableFuture<Float> getFloat(@NotNull String totalID, @NotNull String key);

    /**
     * Asynchronously retrieves a {@link Float} value associated with the specified key.
     *
     * <p>The {@code getFloat} method returns a {@link CompletableFuture} that will be completed with the retrieved
     * {@link Float} value when the operation is successful. If the operation encounters an error, the
     * {@link CompletableFuture} will be completed exceptionally with a {@link Exception}.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * String key = "exampleKey";
     *
     * CompletableFuture<Float> futureResult = someMeta.getFloat(key);
     *
     * // Asynchronously handle the result when it becomes available
     * futureResult.thenAccept(result -> System.out.println("Retrieved value: " + result))
     *             .exceptionally(ex -> {
     *                 System.err.println("Error retrieving value: " + ex.getMessage());
     *                 return null;
     *             });
     * }
     * </pre>
     *
     * @param key the key associated with the value to be retrieved
     * @return a {@link CompletableFuture} that will be completed with the retrieved {@link Float} value,
     * or exceptionally with a {@link Exception} if an error occurs
     * @see #getFloat(String, String)
     * @see MetaWriter#setFloat(String, float)
     * @see MetaWriter#setFloat(String, String, float)
     */
    @NotNull
    CompletableFuture<Float> getFloat(@NotNull String key);

    /**
     * Asynchronously retrieves an {@link Integer} value associated with the specified total ID and key.
     *
     * <p>The {@code getInt} method returns a {@link CompletableFuture} that will be completed with the retrieved
     * {@link Integer} value when the operation is successful. If the operation encounters an error, the
     * {@link CompletableFuture} will be completed exceptionally with a {@link Exception}.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * String totalID = "exampleTotalID";
     * String key = "exampleKey";
     *
     * CompletableFuture<Integer> futureResult = someMeta.getInt(totalID, key);
     *
     * // Asynchronously handle the result when it becomes available
     * futureResult.thenAccept(result -> System.out.println("Retrieved value: " + result))
     *             .exceptionally(ex -> {
     *                 System.err.println("Error retrieving value: " + ex.getMessage());
     *                 return null;
     *             });
     * }
     * </pre>
     *
     * @param totalID the total ID associated with the value to be retrieved
     * @param key     the key associated with the value to be retrieved
     * @return a {@link CompletableFuture} that will be completed with the retrieved {@link Integer} value,
     * or exceptionally with a {@link Exception} if an error occurs
     * @see #getInt(String)
     * @see MetaWriter#setInt(String, int)
     * @see MetaWriter#setInt(String, String, int)
     */
    @NotNull
    CompletableFuture<Integer> getInt(@NotNull String totalID, @NotNull String key);

    /**
     * Asynchronously retrieves an {@link Integer} value associated with the specified key.
     *
     * <p>The {@code getInt} method returns a {@link CompletableFuture} that will be completed with the retrieved
     * {@link Integer} value when the operation is successful. If the operation encounters an error, the
     * {@link CompletableFuture} will be completed exceptionally with a {@link Exception}.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * String key = "exampleKey";
     *
     * CompletableFuture<Integer> futureResult = someMeta.getInt(key);
     *
     * // Asynchronously handle the result when it becomes available
     * futureResult.thenAccept(result -> System.out.println("Retrieved value: " + result))
     *             .exceptionally(ex -> {
     *                 System.err.println("Error retrieving value: " + ex.getMessage());
     *                 return null;
     *             });
     * }
     * </pre>
     *
     * @param key the key associated with the value to be retrieved
     * @return a {@link CompletableFuture} that will be completed with the retrieved {@link Integer} value,
     * or exceptionally with a {@link Exception} if an error occurs
     * @see #getFloat(String, String)
     * @see MetaWriter#setInt(String, int)
     * @see MetaWriter#setInt(String, String, int)
     */
    @NotNull
    CompletableFuture<Integer> getInt(@NotNull String key);

    /**
     * Asynchronously retrieves a {@link Short} value associated with the specified total ID and key.
     *
     * <p>The {@code getShort} method returns a {@link CompletableFuture} that will be completed with the retrieved
     * {@link Short} value when the operation is successful. If the operation encounters an error, the
     * {@link CompletableFuture} will be completed exceptionally with a {@link Exception}.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * String totalID = "exampleTotalID";
     * String key = "exampleKey";
     *
     * CompletableFuture<Short> futureResult = someMeta.getShort(totalID, key);
     *
     * // Asynchronously handle the result when it becomes available
     * futureResult.thenAccept(result -> System.out.println("Retrieved value: " + result))
     *             .exceptionally(ex -> {
     *                 System.err.println("Error retrieving value: " + ex.getMessage());
     *                 return null;
     *             });
     * }
     * </pre>
     *
     * @param totalID the total ID associated with the value to be retrieved
     * @param key     the key associated with the value to be retrieved
     * @return a {@link CompletableFuture} that will be completed with the retrieved {@link Short} value,
     * or exceptionally with a {@link Exception} if an error occurs
     * @see #getShort(String)
     * @see MetaWriter#setShort(String, short)
     * @see MetaWriter#setShort(String, String, short)
     */
    @NotNull
    CompletableFuture<Short> getShort(@NotNull String totalID, @NotNull String key);

    /**
     * Asynchronously retrieves a {@link Short} value associated with the specified key.
     *
     * <p>The {@code getShort} method returns a {@link CompletableFuture} that will be completed with the retrieved
     * {@link Short} value when the operation is successful. If the operation encounters an error, the
     * {@link CompletableFuture} will be completed exceptionally with a {@link Exception}.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * String key = "exampleKey";
     *
     * CompletableFuture<Short> futureResult = someMeta.getShort(key);
     *
     * // Asynchronously handle the result when it becomes available
     * futureResult.thenAccept(result -> System.out.println("Retrieved value: " + result))
     *             .exceptionally(ex -> {
     *                 System.err.println("Error retrieving value: " + ex.getMessage());
     *                 return null;
     *             });
     * }
     * </pre>
     *
     * @param key the key associated with the value to be retrieved
     * @return a {@link CompletableFuture} that will be completed with the retrieved {@link Short} value,
     * or exceptionally with a {@link Exception} if an error occurs
     * @see #getShort(String, String)
     * @see MetaWriter#setShort(String, short)
     * @see MetaWriter#setShort(String, String, short)
     */
    @NotNull
    CompletableFuture<Short> getShort(@NotNull String key);

    /**
     * Asynchronously retrieves a {@link Byte} value associated with the specified total ID and key.
     *
     * <p>The {@code getByte} method returns a {@link CompletableFuture} that will be completed with the retrieved
     * {@link Byte} value when the operation is successful. If the operation encounters an error, the
     * {@link CompletableFuture} will be completed exceptionally with a {@link Exception}.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * String totalID = "exampleTotalID";
     * String key = "exampleKey";
     *
     * CompletableFuture<Byte> futureResult = someMeta.getByte(totalID, key);
     *
     * // Asynchronously handle the result when it becomes available
     * futureResult.thenAccept(result -> System.out.println("Retrieved value: " + result))
     *             .exceptionally(ex -> {
     *                 System.err.println("Error retrieving value: " + ex.getMessage());
     *                 return null;
     *             });
     * }
     * </pre>
     *
     * @param totalID the total ID associated with the value to be retrieved
     * @param key     the key associated with the value to be retrieved
     * @return a {@link CompletableFuture} that will be completed with the retrieved {@link Byte} value,
     * or exceptionally with a {@link Exception} if an error occurs
     * @see #getByte(String)
     * @see MetaWriter#setByte(String, byte)
     * @see MetaWriter#setByte(String, String, byte)
     */
    @NotNull
    CompletableFuture<Byte> getByte(@NotNull String totalID, @NotNull String key);

    /**
     * Asynchronously retrieves a {@link Byte} value associated with the specified key.
     *
     * <p>The {@code getByte} method returns a {@link CompletableFuture} that will be completed with the retrieved
     * {@link Byte} value when the operation is successful. If the operation encounters an error, the
     * {@link CompletableFuture} will be completed exceptionally with a {@link Exception}.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * String key = "exampleKey";
     *
     * CompletableFuture<Byte> futureResult = someMeta.getByte(key);
     *
     * // Asynchronously handle the result when it becomes available
     * futureResult.thenAccept(result -> System.out.println("Retrieved value: " + result))
     *             .exceptionally(ex -> {
     *                 System.err.println("Error retrieving value: " + ex.getMessage());
     *                 return null;
     *             });
     * }
     * </pre>
     *
     * @param key the key associated with the value to be retrieved
     * @return a {@link CompletableFuture} that will be completed with the retrieved {@link Byte} value,
     * or exceptionally with a {@link Exception} if an error occurs
     * @see #getByte(String, String)
     * @see MetaWriter#setByte(String, byte)
     * @see MetaWriter#setByte(String, String, byte)
     */
    @NotNull
    CompletableFuture<Byte> getByte(@NotNull String key);

    /**
     * Asynchronously retrieves a {@link Boolean} value associated with the specified total ID and key.
     *
     * <p>The {@code getBoolean} method returns a {@link CompletableFuture} that will be completed with the retrieved
     * {@link Boolean} value when the operation is successful. If the operation encounters an error, the
     * {@link CompletableFuture} will be completed exceptionally with a {@link Exception}.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * String totalID = "exampleTotalID";
     * String key = "exampleKey";
     *
     * CompletableFuture<Boolean> futureResult = someMeta.getBoolean(totalID, key);
     *
     * // Asynchronously handle the result when it becomes available
     * futureResult.thenAccept(result -> System.out.println("Retrieved value: " + result))
     *             .exceptionally(ex -> {
     *                 System.err.println("Error retrieving value: " + ex.getMessage());
     *                 return null;
     *             });
     * }
     * </pre>
     *
     * @param totalID the total ID associated with the value to be retrieved
     * @param key     the key associated with the value to be retrieved
     * @return a {@link CompletableFuture} that will be completed with the retrieved {@link Boolean} value,
     * or exceptionally with a {@link Exception} if an error occurs
     * @see #getBoolean(String)
     * @see MetaWriter#setBoolean(String, boolean)
     * @see MetaWriter#setBoolean(String, String, boolean)
     */
    @NotNull
    CompletableFuture<Boolean> getBoolean(@NotNull String totalID, @NotNull String key);

    /**
     * Asynchronously retrieves a {@link Boolean} value associated with the specified key.
     *
     * <p>The {@code getBoolean} method returns a {@link CompletableFuture} that will be completed with the retrieved
     * {@link Boolean} value when the operation is successful. If the operation encounters an error, the
     * {@link CompletableFuture} will be completed exceptionally with a {@link Exception}.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * String key = "exampleKey";
     *
     * CompletableFuture<Boolean> futureResult = someMeta.getBoolean(key);
     *
     * // Asynchronously handle the result when it becomes available
     * futureResult.thenAccept(result -> System.out.println("Retrieved value: " + result))
     *             .exceptionally(ex -> {
     *                 System.err.println("Error retrieving value: " + ex.getMessage());
     *                 return null;
     *             });
     * }
     * </pre>
     *
     * @param key the key associated with the value to be retrieved
     * @return a {@link CompletableFuture} that will be completed with the retrieved {@link Boolean} value,
     * or exceptionally with a {@link Exception} if an error occurs
     * @see #getBoolean(String, String)
     * @see MetaWriter#setBoolean(String, boolean)
     * @see MetaWriter#setBoolean(String, String, boolean)
     */
    @NotNull
    CompletableFuture<Boolean> getBoolean(@NotNull String key);

    /**
     * Asynchronously retrieves a {@link Collection} of objects of type {@code T} associated with the specified total ID and key.
     *
     * <p>The {@code getCollection} method returns a {@link CompletableFuture} that will be completed with the retrieved
     * {@link Collection} when the operation is successful. If the operation encounters an error, the
     * {@link CompletableFuture} will be completed exceptionally with a {@link Exception}.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * String totalID = "exampleTotalID";
     * String key = "exampleKey";
     * Class<String> recordType = String.class;
     *
     * CompletableFuture<Collection<String>> futureResult = someMeta.getCollection(totalID, key, recordType);
     *
     * // Asynchronously handle the result when it becomes available
     * futureResult.thenAccept(result -> System.out.println("Retrieved collection: " + result))
     *             .exceptionally(ex -> {
     *                 System.err.println("Error retrieving collection: " + ex.getMessage());
     *                 return null;
     *             });
     * }
     * </pre>
     *
     * @param totalID the total ID associated with the collection to be retrieved
     * @param key     the key associated with the collection to be retrieved
     * @param record  the class type of the elements in the collection
     * @param <T>     the type of elements in the collection
     * @return a {@link CompletableFuture} that will be completed with the retrieved {@link Collection} of type {@code T},
     * or exceptionally with a {@link Exception} if an error occurs
     * @see #getCollection(String, Class)
     * @see MetaWriter#setCollection(String, Collection)
     * @see MetaWriter#setCollection(String, String, Collection)
     */
    <T> @NotNull CompletableFuture<MetaList<T>> getCollection(@NotNull String totalID, @NotNull String key, @NotNull Class<T> record);

    /**
     * Asynchronously retrieves a {@link Collection} of objects of type {@code T} associated with the specified key.
     *
     * <p>The {@code getCollection} method returns a {@link CompletableFuture} that will be completed with the retrieved
     * {@link Collection} when the operation is successful. If the operation encounters an error, the
     * {@link CompletableFuture} will be completed exceptionally with a {@link Exception}.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * String key = "exampleKey";
     * Class<String> recordType = String.class;
     *
     * CompletableFuture<Collection<String>> futureResult = someMeta.getCollection(key, recordType);
     *
     * // Asynchronously handle the result when it becomes available
     * futureResult.thenAccept(result -> System.out.println("Retrieved collection: " + result))
     *             .exceptionally(ex -> {
     *                 System.err.println("Error retrieving collection: " + ex.getMessage());
     *                 return null;
     *             });
     * }
     * </pre>
     *
     * @param key  the key associated with the collection to be retrieved
     * @param type the class type of the elements in the collection
     * @param <T>  the type of elements in the collection
     * @return a {@link CompletableFuture} that will be completed with the retrieved {@link Collection} of type {@code T},
     * or exceptionally with a {@link Exception} if an error occurs
     * @see #getCollection(String, String, Class)
     * @see MetaWriter#setCollection(String, Collection)
     * @see MetaWriter#setCollection(String, String, Collection)
     */
    <T> @NotNull CompletableFuture<MetaList<T>> getCollection(@NotNull String key, @NotNull Class<T> type);

    /**
     * Asynchronously retrieves an object of type {@code T} associated with the specified total ID and key.
     *
     * <p>The {@code getObject} method returns a {@link CompletableFuture} that will be completed with the retrieved
     * object of type {@code T} when the operation is successful. If the operation encounters an error, the
     * {@link CompletableFuture} will be completed exceptionally with a {@link Exception}.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * String totalID = "exampleTotalID";
     * String key = "exampleKey";
     * Class<String> objectType = String.class;
     *
     * CompletableFuture<String> futureResult = someMeta.getObject(totalID, key, objectType);
     *
     * // Asynchronously handle the result when it becomes available
     * futureResult.thenAccept(result -> System.out.println("Retrieved object: " + result))
     *             .exceptionally(ex -> {
     *                 System.err.println("Error retrieving object: " + ex.getMessage());
     *                 return null;
     *             });
     * }
     * </pre>
     *
     * @param totalID the total ID associated with the object to be retrieved
     * @param key     the key associated with the object to be retrieved
     * @param type    the class type of the object to be retrieved
     * @param <T>     the type of the object to be retrieved
     * @return a {@link CompletableFuture} that will be completed with the retrieved object of type {@code T},
     * or exceptionally with a {@link Exception} if an error occurs
     * @see #getObject(String, Class)
     * @see MetaWriter#setObject(String, Object)
     * @see MetaWriter#setObject(String, String, Object)
     */
    <T> @NotNull CompletableFuture<T> getObject(@NotNull String totalID, @NotNull String key, @NotNull Class<T> type);

    /**
     * Asynchronously retrieves an object of type {@code T} associated with the specified key.
     *
     * <p>The {@code getObject} method returns a {@link CompletableFuture} that will be completed with the retrieved
     * object of type {@code T} when the operation is successful. If the operation encounters an error, the
     * {@link CompletableFuture} will be completed exceptionally with a {@link Exception}.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * String key = "exampleKey";
     * Class<String> objectType = String.class;
     *
     * CompletableFuture<String> futureResult = someMeta.getObject(key, objectType);
     *
     * // Asynchronously handle the result when it becomes available
     * futureResult.thenAccept(result -> System.out.println("Retrieved object: " + result))
     *             .exceptionally(ex -> {
     *                 System.err.println("Error retrieving object: " + ex.getMessage());
     *                 return null;
     *             });
     * }
     * </pre>
     *
     * @param key  the key associated with the object to be retrieved
     * @param type the class type of the object to be retrieved
     * @param <T>  the type of the object to be retrieved
     * @return a {@link CompletableFuture} that will be completed with the retrieved object of type {@code T},
     * or exceptionally with a {@link Exception} if an error occurs
     * @see #getObject(String, String, Class)
     * @see MetaWriter#setObject(String, Object)
     * @see MetaWriter#setObject(String, String, Object)
     */
    <T> @NotNull CompletableFuture<T> getObject(@NotNull String key, @NotNull Class<T> type);

    /**
     * Creates and returns a root query select with the specified columns.
     *
     * <p>The {@code select} method returns a {@link QuerySelect.RootQuerySelect} that can be further configured with the
     * provided {@link Column} instances. It allows selecting data from the underlying data store based on the specified columns.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * Column column1 = new SomeColumn();
     * Column column2 = new AnotherColumn();
     *
     * QuerySelect.RootQuerySelect rootQuerySelect = someQueryManager.select(column1, column2);
     *
     * // Configure and execute the select operation
     * // (Additional columns or configuration can be applied to rootQuerySelect)
     * rootQuerySelect.execute();
     * }
     * </pre>
     *
     * @param columns the columns to be selected in the root query select
     * @return a {@link QuerySelect.RootQuerySelect} configured with the specified columns
     */
    @NotNull
    QuerySelect.RootQuerySelect select(@NotNull Column... columns);

    /**
     * Retrieves the data associated with the object identified by the specified total ID.
     *
     * <p>The totalId parameter represents the total ID of the object for which the data will be retrieved.
     * It is a non-null string that uniquely identifies the object across the system.
     * The method returns an optional SavableMap object that contains the data associated with the object.
     * If no data is found for the specified total ID, an empty optional will be returned.
     *
     * @param totalId the total ID of the object for which the data will be retrieved.
     * @return an optional SavableMap object containing the data associated with the object, or an empty optional if
     * no data is found.
     */
    @NotNull CompletableFuture<SavableMap> getData(@NotNull String totalId);

    /**
     * Retrieves the data associated with the specified Savable object.
     *
     * <p>The savable parameter represents a Savable object for which the data will be retrieved.
     * It should be a non-null object implementing the Savable interface.
     * The method returns a map of SavableMap objects that represent the data associated with the specified Savable
     * object.
     * The map contains mappings between string IDs and their corresponding SavableMap objects.
     * If no data is found for the specified Savable object, an empty map will be returned.
     * The returned map is unmodifiable to maintain data integrity and prevent unauthorized modifications.
     *
     * @param savable the Savable object for which the data will be retrieved.
     * @param <T>     the type of the Savable object.
     * @return an unmodifiable map of SavableMap objects representing the data associated with the specified Savable
     * object.
     * If no data is found for the specified Savable object, an empty map is returned.
     * @see Savable
     */
    @NotNull CompletableFuture<@Unmodifiable Map<@NotNull String, @NotNull SavableMap>> getData(@NotNull Savable<?> savable);
}
