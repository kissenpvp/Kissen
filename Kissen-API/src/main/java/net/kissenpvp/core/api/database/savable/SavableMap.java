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

import net.kissenpvp.core.api.database.meta.ObjectMeta;
import net.kissenpvp.core.api.database.savable.list.KissenList;
import net.kissenpvp.core.api.database.savable.list.SavableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The `SavableMap` interface extends the standard `Map` interface and provides additional functionality for
 * a map-based data structure that can be saved and synchronized with a database.
 *
 * <p>The `SavableMap` interface inherits all the methods from the `Map` interface, allowing key-value pairs to
 * be stored and retrieved. In addition, it extends the `Cloneable` and `Serializable` interfaces to support
 * cloning and serialization capabilities.</p>
 *
 * <p>Example usage:</p>
 *
 * <pre>
 * {@code
 * SavableMap savableMap = new SomeSavableMapImplementation();
 * savableMap.put("key1", "value1");
 * savableMap.put("key2", "value2");
 * String value = savableMap.get("key1");
 * System.out.println(value); // Output: "value1"
 * }
 * </pre>
 *
 * <p>The `SavableMap` interface is designed to be used as a map-like data structure that can be saved and
 * synchronized with a database. It allows key-value pairs to be stored and retrieved using the methods defined
 * in the `Map` interface. Additionally, it provides methods for saving, synchronizing, and managing the map data.</p>
 *
 * <p>Implementations of the `SavableMap` interface should provide implementations for all the methods defined in
 * the `Map` interface. They should also support the cloning and serialization capabilities provided by the
 * `Cloneable` and `Serializable` interfaces.</p>
 *
 * <p>It's important to note that the `SavableMap` interface does not define the underlying database or storage
 * mechanism. It serves as a contract for a map-based data structure that can be saved and synchronized, and the
 * specific implementation may vary depending on the database or storage technology used.</p>
 *
 * <p>This interface should be implemented by classes that represent map-based data structures that can be saved
 * and synchronized with a database. It extends the `Map` interface and provides additional functionality for
 * managing the map data.</p>
 *
 * @see Map
 * @see Cloneable
 * @see Serializable
 */
public interface SavableMap extends Map<String, Object>, Serializable {

    /**
     * Copies all the key-value pairs from the specified {@link SavableMap} to this map.
     *
     * <p>The {@code putAll(SavableMap)} method copies all the key-value pairs from the specified {@link SavableMap} to this map,
     * effectively adding or updating the entries. Unlike the {@link #set(String, Object)} method, the {@code putAll(SavableMap)} method does not
     * alter the underlying table or storage mechanism. It only affects the contents of this map.</p>
     *
     * <p>The specified {@link SavableMap} contains key-value pairs that will be copied to this map. If a key already
     * exists in this map, the corresponding value will be updated. If a key does not exist, a new entry will
     * be added to this map.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * SavableMap sourceMap = new SomeSavableMapImplementation();
     * sourceMap.put("key1", "value1");
     * sourceMap.put("key2", "value2");
     *
     * SavableMap targetMap = new SomeSavableMapImplementation();
     * targetMap.putAll(sourceMap);
     *
     * Optional<String> value = targetMap.get("key1");
     * System.out.println(value.orElse(null)); // Output: "value1"
     * }
     * </pre>
     *
     * @param savableMap the {@link SavableMap} containing the key-value pairs to be copied to this map
     * @throws NullPointerException if the specified {@link SavableMap} is `null`
     * @see #put(Object, Object)
     */
    void putAll(@NotNull SavableMap savableMap);

    <T> @Nullable Object set(@NotNull String key, @Nullable T value);


    <T> void setIfAbsent(@NotNull String key, @NotNull T value);

    /**
     * Removes the value associated with the given key from the table.
     *
     * <p>The `delete()` method removes the value associated with the given key from the table. If the key exists
     * in the table, the method deletes the corresponding entry and removes the association between the key and
     * the value. If the key is not present in the table, the method does nothing and returns.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * SavableMap savableMap = new SomeSavableMapImplementation();
     * savableMap.set("key1", "value1");
     * savableMap.set("key2", "value2");
     *
     * savableMap.delete("key1"); // Remove the entry associated with "key1"
     *
     * Optional<String> value1 = savableMap.get("key1");
     * System.out.println(value1.orElse(null)); // Output: null, as the entry was deleted
     *
     * Optional<String> value2 = savableMap.get("key2");
     * System.out.println(value2.orElse(null)); // Output: "value2"
     * }
     * </pre>
     *
     * <p>It's important to note that the `delete()` method removes the entry associated with the given key from
     * the table. Depending on the implementation, this may involve deleting the corresponding row in the database
     * or performing other storage-specific operations.</p>
     *
     * @param key the key of the object to be deleted
     * @return
     * @throws NullPointerException if the specified key is `null`
     */
    @Nullable Object delete(@NotNull String key);


    <T> @NotNull Optional<T> get(@NotNull String key, @NotNull Class<T> clazz);


    <T> @NotNull T getNotNull(@NotNull String key, @NotNull Class<T> record) throws NoSuchFieldError;

    /**
     * Checks if a list exists in the table associated with the given key.
     *
     * <p>The {@code containsList()} method checks if a list exists in the table with the specified key. It returns
     * {@code true} if a list is associated with the key, and {@code false} otherwise.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>{@code
     * SavableMap savableMap = new SomeSavableMapImplementation();
     * List (String) list = Arrays.asList("item1", "item2", "item3");
     * savableMap.setList("key1", list);
     *
     * boolean containsList = savableMap.containsList("key1");
     * System.out.println(containsList); // Output: true
     *
     * boolean containsList2 = savableMap.containsList("key2");
     * System.out.println(containsList2); // Output: false, as the key does not exist
     * }</pre>
     *
     * <p>It's important to note that the {@code containsList()} method checks specifically for the existence of a list
     * associated with the given key. It does not perform a general check for the presence of any value.</p>
     *
     * @param key the key to check for the existence of a list
     * @return {@code true} if a list is associated with the key, {@code false} otherwise
     * @throws NullPointerException if the specified key is {@code null}
     */
    boolean containsList(@NotNull String key);

    @NotNull <T> Optional<KissenList<T>> getList(@NotNull String key, Class<T> type);

    /**
     * Retrieves a List of Strings from the table associated with the specified key.
     *
     * <p>The `getListNotNull()` method retrieves a List of Strings from the table based on the given key. If a list
     * exists
     * for the specified key, it is returned. If no list is found for the key, a new empty list is automatically
     * generated
     * and returned.</p>
     *
     * <p>The specified key represents the key of the list to retrieve from the table. If a list exists for the key, it
     * will be returned. If no list is found, a new empty list is generated and returned.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * SavableMap savableMap = new SomeSavableMapImplementation();
     * List<String> list = Arrays.asList("item1", "item2", "item3");
     * savableMap.setList("key1", list);
     *
     * SavableList retrievedList = savableMap.getListNotNull("key1");
     * System.out.println(retrievedList); // Output: ["item1", "item2", "item3"]
     *
     * SavableList nonExistentList = savableMap.getListNotNull("key2");
     * System.out.println(nonExistentList); // Output: []
     * }
     * </pre>
     *
     * @param key the key of the list to retrieve
     * @return the List of Strings associated with the key, if it exists, or a new empty list if it doesn't
     * @throws NullPointerException if the specified key is `null`
     * @see SavableList
     */
    @NotNull <T> KissenList<T> getListNotNull(@NotNull String key, @NotNull Class<T> type);

    /**
     * Inserts a List of Strings into the table associated with the specified key. The list will not be saved
     * permanently.
     *
     * <p>The `putList()` method allows you to store a List of Strings in the table using the provided key. The list
     * provided as the value will be associated with the specified key, but it will not be saved permanently in the
     * database. This means that the list will not persist across sessions or database updates.</p>
     *
     * <p>The specified key represents the key to associate with the provided list. When you retrieve the value using
     * the key later, you will get the same list object that was originally provided to the method. However, please note
     * that the list itself is not saved in the database and is not synchronized with it.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * SavableMap savableMap = new SomeSavableMapImplementation();
     * List<String> list = Arrays.asList("item1", "item2", "item3");
     * savableMap.putList("key1", list);
     *
     * SavableList retrievedList = savableMap.getListNotNull("key1");
     * System.out.println(retrievedList); // Output: ["item1", "item2", "item3"]
     * }
     * </pre>
     *
     * @param key   the key to associate with the list
     * @param value the List of Strings to be inserted
     * @return the SavableList associated with the key
     * @throws NullPointerException if the specified key is `null`
     * @see #setList(String, Collection)
     */
    <T> @Nullable Object putList(@NotNull String key, @Nullable Collection<T> value);


    <T> @Nullable @Unmodifiable List<T> putListValue(@NotNull String key, @NotNull T value);

    <T> @Nullable Object putListIfAbsent(@NotNull String key, @NotNull Collection<T> value);

    <T> @Nullable Object setList(@NotNull String key, @Nullable Collection<T> value);

    <T> @Nullable @Unmodifiable List<T> setListValue(@NotNull String key, @NotNull T value);

    <T> @Nullable Object setListIfAbsent(@NotNull String key, @NotNull Collection<T> value);


    /**
     * Removes the list associated with the specified key from the table. The list will not be saved permanently.
     *
     * <p>The `removeList()` method allows you to remove the list associated with the provided key from the map. This
     * action does not permanently affect the map, and won't be noted across multiple sessions. In order to delete it
     * also in the database use {@link #delete(String)}.</p>
     *
     * <p>The specified key represents the key associated with the list to be removed. If the key exists in the table
     * and is associated with a list, the list will be removed. If the key is not present in the table or is not
     * associated
     * with a list, no action will be taken.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * SavableMap savableMap = new SomeSavableMapImplementation();
     * List<String> existingList = Arrays.asList("item1", "item2");
     * savableMap.putList("key1", existingList);
     *
     * savableMap.removeList("key1");
     * System.out.println(savableMap.getList("key1")); // Output: null, as the list has been removed
     *
     * savableMap.removeList("key2");
     * System.out.println(savableMap.getList("key2")); // Output: null, as the key does not exist
     * }
     * </pre>
     *
     * @param key the key associated with the list to be removed
     * @return
     * @throws NullPointerException if the specified key is `null`
     * @see #deleteList(String)
     */
    boolean removeList(@NotNull String key);

    /**
     * Removes the specified value from the list associated with the provided key. The removal does not persist the
     * changes.
     *
     * <p>The `removeListValue()` method allows you to remove the specified value from the list associated with the
     * provided key in the table. This action does not permanently save the changes, and it will only affect the list in
     * memory.</p>
     *
     * <p>The specified key represents the key associated with the list. If the key exists in the table and is
     * associated
     * with a list, the specified value will be removed from the list. If the key is not present in the table or is not
     * associated with a list, no action will be taken.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * SavableMap savableMap = new SomeSavableMapImplementation();
     * List<String> existingList = Arrays.asList("item1", "item2", "item3");
     * savableMap.putList("key1", existingList);
     *
     * savableMap.removeListValue("key1", "item2");
     * System.out.println(savableMap.getList("key1")); // Output: ["item1", "item3"], as "item2" has been removed
     *
     * savableMap.removeListValue("key2", "item1");
     * System.out.println(savableMap.getList("key2")); // Output: null, as the key does not exist
     * }
     * </pre>
     *
     * <p>It's important to note that this action does not persist the changes. If you want to remove the value from the
     * list and also save the changes permanently, you should use the {@link #deleteListValue(String, T)} method
     * instead.</p>
     *
     * @param key   the key associated with the list
     * @param value the value to remove from the list
     * @throws NullPointerException if the specified key or value is `null`
     * @see #deleteListValue(String, T)
     */
    <T> boolean removeListValue(@NotNull String key, @NotNull T value);

    /**
     * Removes the specified value from the list associated with the provided key. The removal will be saved
     * permanently.
     *
     * <p>The `deleteListValue()` method allows you to remove the specified value from the list associated with the
     * provided key in the table. This action permanently removes the value from the list in the table and saves the
     * changes.</p>
     *
     * <p>The specified key represents the key associated with the list. If the key exists in the table and is
     * associated
     * with a list, the specified value will be removed from the list. If the key is not present in the table or is not
     * associated with a list, no action will be taken.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * SavableMap savableMap = new SomeSavableMapImplementation();
     * List<String> existingList = Arrays.asList("item1", "item2", "item3");
     * savableMap.putList("key1", existingList);
     *
     * savableMap.deleteListValue("key1", "item2");
     * System.out.println(savableMap.getList("key1")); // Output: ["item1", "item3"], as "item2" has been removed
     *
     * savableMap.deleteListValue("key2", "item1");
     * System.out.println(savableMap.getList("key2")); // Output: null, as the key does not exist
     * }
     * </pre>
     *
     * @param key   the key associated with the list
     * @param value the value to remove from the list
     * @throws NullPointerException if the specified key or value is `null`
     * @see #removeListValue(String, T)
     */
    <T> boolean deleteListValue(@NotNull String key, @NotNull T value);

    /**
     * Removes the list associated with the specified key from the table. The list will be saved permanently.
     *
     * <p>The `deleteList()` method allows you to remove the list associated with the provided key from the table. This
     * action permanently deletes the list from the table and saves the changes.</p>
     *
     * <p>The specified key represents the key associated with the list to be removed. If the key exists in the table
     * and
     * is associated with a list, the list will be deleted from the table. If the key is not present in the table or is
     * not associated with a list, no action will be taken.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * SavableMap savableMap = new SomeSavableMapImplementation();
     * List<String> existingList = Arrays.asList("item1", "item2", "item3");
     * savableMap.putList("key1", existingList);
     *
     * savableMap.deleteList("key1");
     * System.out.println(savableMap.getList("key1")); // Output: null, as the list has been deleted
     *
     * savableMap.deleteList("key2");
     * System.out.println(savableMap.getList("key2")); // Output: null, as the key does not exist
     * }
     * </pre>
     *
     * @param key the key associated with the list to be removed
     * @throws NullPointerException if the specified key is `null`
     * @see #removeList(String)
     */
    boolean deleteList(@NotNull String key);

    /**
     * Returns a serializable version of the SavableMap.
     *
     * <p>The `toSerializable()` method allows you to obtain a serializable version of the SavableMap. This can be
     * useful
     * when you need to serialize the map and store it or transfer it over a network.</p>
     *
     * <p>The returned `SavableMap` object is a serializable representation of the original map. It contains the same
     * key-value
     * pairs and maintains any modifications made to the original map.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * SavableMap savableMap = new SomeSavableMapImplementation();
     * savableMap.set("key1", "value1");
     * savableMap.set("key2", "value2");
     *
     * // Obtain the serializable version of the map
     * SavableMap serializableMap = savableMap.toSerializable();
     * }
     * </pre>
     *
     * @return a serializable version of the SavableMap
     */
    @NotNull SavableMap serializeSavable();

    @NotNull ObjectMeta getMeta();
}
