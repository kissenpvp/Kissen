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
import net.kissenpvp.core.api.database.savable.list.SavableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
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
public interface SavableMap extends Map<String, String>, Serializable {
    /**
     * Copies all the key-value pairs from the specified `SavableMap` to this map.
     *
     * <p>The `putAll()` method copies all the key-value pairs from the specified `SavableMap` to this map,
     * effectively adding or updating the entries. Unlike the `set()` method, the `putAll()` method does not
     * alter the underlying table or storage mechanism. It only affects the contents of this map.</p>
     *
     * <p>The specified `SavableMap` contains key-value pairs that will be copied to this map. If a key already
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
     * @param savableMap the `SavableMap` containing the key-value pairs to be copied to this map
     * @throws NullPointerException if the specified `SavableMap` is `null`
     * @see #put(Object, Object)
     */
    void putAll(@NotNull SavableMap savableMap);

    /**
     * Saves the specified value in the table associated with the given key.
     *
     * <p>The `set()` method saves the specified value in the table associated with the given key. It updates the
     * entry by associating the key with the provided value. If the
     * value is `null`, the corresponding entry will be deleted from the table. However, it is generally recommended
     * to use the {@link #delete(String)} method to explicitly delete entries, as it provides better clarity and
     * intent for deletion operations.</p>
     *
     * <p>The specified key represents the key of the entry to be updated or deleted, while the value represents
     * the string value to be stored. If the key already exists in the table, the corresponding value will be
     * updated.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * SavableMap savableMap = new SomeSavableMapImplementation();
     * savableMap.set("key1", "value1");
     * savableMap.set("key2", "value2");
     *
     * Optional<String> value = savableMap.get("key1");
     * System.out.println(value.orElse(null)); // Output: "value1"
     *
     * savableMap.set("key1", null); // Remove the entry associated with "key1"
     * value = savableMap.get("key1");
     * System.out.println(value.orElse(null)); // Output: null
     * }
     * </pre>
     *
     * <p>It's important to note that the `set()` method directly saves the given string value in the table and
     * deletes the entry if the value is `null`. Depending on the implementation, this may involve updating the
     * database or performing other storage-specific operations.</p>
     *
     * @param key   the key of the entry to be updated or deleted
     * @param value the string value to be stored or `null` to delete the entry
     * @throws NullPointerException if the specified key is `null`
     * @see #put(Object, Object)
     * @see #delete(String)
     */
    void set(@NotNull String key, @Nullable String value);

    /**
     * Sets the specified value in the table associated with the given key, only if there is no existing value
     * associated with the key.
     *
     * <p>The `setIfAbsent()` method sets the specified value in the table associated with the given key, but
     * only if there is no existing value associated with the key. If the key already exists in the table, the
     * method does nothing and leaves the existing value unchanged.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * SavableMap savableMap = new SomeSavableMapImplementation();
     * savableMap.setIfAbsent("key1", "value1"); // Set the value only if "key1" doesn't exist
     * savableMap.setIfAbsent("key2", "value2"); // Set the value only if "key2" doesn't exist
     * savableMap.setIfAbsent("key1", "new value"); // "key1" already exists, so the value remains unchanged
     *
     * Optional<String> value1 = savableMap.get("key1");
     * System.out.println(value1.orElse(null)); // Output: "value1"
     *
     * Optional<String> value2 = savableMap.get("key2");
     * System.out.println(value2.orElse(null)); // Output: "value2"
     * }
     * </pre>
     *
     * @param key   the key of the value to be set
     * @param value the value to be set if there is no existing value associated with the key
     * @throws NullPointerException if the specified key is `null`
     */
    void setIfAbsent(@NotNull String key, @NotNull String value);

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
     * @throws NullPointerException if the specified key is `null`
     */
    void delete(@NotNull String key);

    /**
     * Retrieves the value associated with the given key from the table and returns it as an {@link Optional}.
     *
     * <p>The {@code get()} method retrieves the value associated with the specified key from the table. If the key
     * exists
     * in the table and is associated with a value, that value is returned wrapped in an {@link Optional}. If the key
     * is not
     * present in the table or if the value associated with the key is {@code null}, an empty {@link Optional} is
     * returned.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * SavableMap savableMap = new SomeSavableMapImplementation();
     * savableMap.set("key1", "value1");
     * savableMap.set("key2", "value2");
     *
     * Optional<String> value1 = savableMap.get("key1");
     * System.out.println(value1.orElse("Key not found")); // Output: "value1"
     *
     * Optional<String> value4 = savableMap.get("key4");
     * System.out.println(value4.orElse("Key not found")); // Output: "Key not found", as the key does not exist
     * }
     * </pre>
     *
     * <p>Additionally, an alternative method {@link #getNotNull(String)} is available to retrieve the value associated
     * with the key and guarantee a non-null return value. Please use {@link #getNotNull(String)} if you expect the
     * value
     * to be non-null and want to handle a potential {@link NullPointerException} instead of using {@link Optional}.</p>
     *
     * @param key the key of the object to retrieve
     * @return an {@link Optional} containing the value associated with the key, or an empty {@link Optional} if the
     * key is
     * not present or the value is {@code null}
     * @throws NullPointerException if the specified key is {@code null}
     * @see Optional
     * @see #getNotNull(String)
     */
    @NotNull Optional<String> get(@NotNull String key);

    /**
     * Retrieves the value associated with the given key from the table and returns it as a non-null string.
     *
     * <p>The {@code getNotNull()} method is similar to the {@link #get(String)} method, but it guarantees that a
     * non-null
     * value will be returned. If the key exists in the table and is associated with a non-null value, that value is
     * returned. If the key is not present in the table or if the value associated with the key is {@code null}, a
     * {@link NullPointerException} is thrown.</p>
     *
     * <p>This method provides a convenient way to retrieve a non-null value from the table when a null value is not
     * expected. If a null value is encountered and handling it is required, the {@link #get(String)} method should be
     * used instead.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * SavableMap savableMap = new SomeSavableMapImplementation();
     * savableMap.set("key1", "value1");
     * savableMap.set("key2", "value2");
     *
     * String value1 = savableMap.getNotNull("key1");
     * System.out.println(value1); // Output: "value1"
     *
     * String value4 = savableMap.getNotNull("key4"); // Throws NullPointerException
     * }
     * </pre>
     *
     * <p>This method is related to the {@link #get(String)} method, providing a non-null value guarantee for
     * convenience
     * and compile-time safety.</p>
     *
     * @param key the key of the object to retrieve
     * @return the value associated with the key as a non-null string
     * @throws NullPointerException if the specified key is not present or the associated value is null
     * @see #get(String)
     */
    @NotNull String getNotNull(@NotNull String key) throws NullPointerException;

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

    /**
     * Retrieves a list of strings associated with the specified key from the table.
     *
     * <p>The {@code getList()} method retrieves a list of strings that is associated with the given key in the table.
     * If a list is associated with the key, it will be returned as an {@code Optional}. If the key does not exist in
     * the
     * table or the associated value is {@code null}, an empty {@code Optional} is returned.</p>
     *
     * <p>It's important to note that the retrieved list is synchronized with the underlying database. Any modifications
     * made to the list will be reflected in the database. Therefore, it's recommended to use this method
     * when working with lists that are synchronized with the database.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>{@code
     * SavableMap savableMap = new SomeSavableMapImplementation();
     * List<String> list = Arrays.asList("item1", "item2", "item3");
     * savableMap.set("key1", list);
     *
     * Optional<SavableList> retrievedList = savableMap.getList("key1");
     * if (retrievedList.isPresent())
     * {
     *     SavableList list = retrievedList.get();
     *     System.out.println(list); // Output: ["item1", "item2", "item3"]
     * }
     * else
     * {
     *     System.out.println("List does not exist.");
     * }
     *
     * Optional<SavableList> retrievedList2 = savableMap.getList("key2");
     * if (retrievedList2.isPresent())
     * {
     *     SavableList list = retrievedList2.get();
     *     System.out.println(list);
     * }
     * else
     * {
     *     System.out.println("List does not exist."); // Output: "List does not exist."
     * }
     * }</pre>
     *
     * @param key the key of the list to retrieve
     * @return an {@code Optional} containing the list of strings associated with the key, or an empty {@code
     * Optional} if the key does not exist or the value is {@code null}
     * @throws NullPointerException if the specified key is {@code null}
     * @see SavableList
     */
    @NotNull Optional<SavableList> getList(@NotNull String key);

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
    @NotNull SavableList getListNotNull(@NotNull String key);

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
     * @see #setList(String, List)
     */
    @NotNull SavableList putList(@NotNull String key, @Nullable List<String> value);


    /**
     * Adds a value to the list associated with the specified key. The value will not be saved permanently.
     *
     * <p>The `putListValue()` method allows you to add a value to the list associated with the provided key. The value
     * you provide will be appended to the existing list, but it will not be saved permanently in the database. This
     * means
     * that the value will not persist across sessions or database updates.</p>
     *
     * <p>The specified key represents the key associated with the list. When you retrieve the list using the key later,
     * you will get the updated list with the added value. However, please note that the list itself is not saved in the
     * database and is not synchronized with it.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * SavableMap savableMap = new SomeSavableMapImplementation();
     * List<String> list = Arrays.asList("item1", "item2");
     * savableMap.putList("key1", list);
     *
     * savableMap.putListValue("key1", "item3");
     * SavableList retrievedList = savableMap.getListNotNull("key1");
     * System.out.println(retrievedList); // Output: ["item1", "item2", "item3"]
     * }
     * </pre>
     *
     * <p>Note that the value provided to the `putListValue()` method will not be saved permanently in
     * the database. If you need to persist the list or values across sessions or database updates, you should
     * consider using {@link #setListValue(String, String)} or mechanisms for data storage.</p>
     *
     * @param key   the key associated with the list
     * @param value the value to be added to the list
     * @return the SavableList associated with the key
     * @throws NullPointerException if the specified key or value is `null`
     * @see #setListValue(String, String)
     */
    @NotNull SavableList putListValue(@NotNull String key, @NotNull String value);

    /**
     * Adds a value to the list associated with the specified key, if the list does not already exist. The value will
     * not
     * be saved permanently.
     *
     * <p>The `putListIfAbsent()` method allows you to add a value to the list associated with the provided key, only if
     * the list does not already exist. If the list already exists, the method does not modify the existing list and
     * returns the existing list. The value you provide will only be added to a newly created list if the list does not
     * exist.</p>
     *
     * <p>The specified key represents the key associated with the list. When you retrieve the list using the key later,
     * you will get the updated list with the added value. However, please note that the list itself is not saved in the
     * database and is not synchronized with it.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * SavableMap savableMap = new SomeSavableMapImplementation();
     * List<String> existingList = Arrays.asList("item1", "item2");
     * savableMap.putList("key1", existingList);
     *
     * List<String> newList = Arrays.asList("item3", "item4");
     * SavableList updatedList = savableMap.putListIfAbsent("key1", newList);
     * System.out.println(updatedList); // Output: ["item1", "item2"], as the list already existed
     *
     * SavableList retrievedList = savableMap.getListNotNull("key1");
     * System.out.println(retrievedList); // Output: ["item1", "item2"], the existing list
     * }
     * </pre>
     *
     * <p>Make sure to note that the value provided to the `putListIfAbsent()` method will only be added to a newly
     * created list if the list does not already exist. If you need to persist the list or values across sessions or
     * database updates, you should consider using {@link #setListIfAbsent(String, List)} or mechanisms for data
     * storage.</p>
     *
     * @param key   the key associated with the list
     * @param value the value to be added to the list
     * @return the SavableList associated with the key, either the existing list or a newly created list with the
     * added value
     * @throws NullPointerException if the specified key or value is `null`
     * @see #setListIfAbsent(String, List)
     */
    @NotNull SavableList putListIfAbsent(@NotNull String key, @NotNull List<String> value);

    /**
     * Sets the list associated with the specified key to a new value. The new value will be saved permanently in the
     * table.
     *
     * <p>The `setList()` method allows you to set the list associated with the provided key to a new value. The
     * existing
     * list, if any, will be replaced with the new value. The updated list will be saved permanently in the table.</p>
     *
     * <p>The specified key represents the key associated with the list. When you retrieve the list using the key later,
     * you will get the updated list with the new value. The list will be saved in the database and synchronized with
     * it.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * SavableMap savableMap = new SomeSavableMapImplementation();
     * List<String> existingList = Arrays.asList("item1", "item2");
     * savableMap.putList("key1", existingList);
     *
     * List<String> newList = Arrays.asList("item3", "item4");
     * SavableList updatedList = savableMap.setList("key1", newList);
     * System.out.println(updatedList); // Output: ["item3", "item4"], the new list
     *
     * SavableList retrievedList = savableMap.getListNotNull("key1");
     * System.out.println(retrievedList); // Output: ["item3", "item4"], the updated list
     * }
     * </pre>
     *
     * @param key   the key associated with the list
     * @param value the new value to be set for the list
     * @return the SavableList associated with the key, representing the updated list
     * @throws NullPointerException if the specified key is `null`
     * @see #putList(String, List)
     */
    @NotNull SavableList setList(@NotNull String key, @Nullable List<String> value);

    /**
     * Sets the list associated with the specified key to a new value if the key is not already present. The new value
     * will be saved permanently in the table.
     *
     * <p>The `setListIfAbsent()` method allows you to set the list associated with the provided key to a new value
     * if the
     * key is not already present in the table. If the key already exists in the table, the method will not modify the
     * existing list. If the key is not present, the new value will be set as the list associated with the key and saved
     * permanently in the table.</p>
     *
     * <p>The specified key represents the key associated with the list. When you retrieve the list using the key later,
     * you will get either the existing list if the key was already present or the newly set list if the key was not
     * present.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * SavableMap savableMap = new SomeSavableMapImplementation();
     * List<String> existingList = Arrays.asList("item1", "item2");
     * savableMap.putList("key1", existingList);
     *
     * List<String> newList = Arrays.asList("item3", "item4");
     * SavableList updatedList = savableMap.setListIfAbsent("key1", newList);
     * System.out.println(updatedList); // Output: ["item1", "item2"], the existing list, as the key was already present
     *
     * SavableList newList = savableMap.setListIfAbsent("key2", newList);
     * System.out.println(newList); // Output: ["item3", "item4"], the newly set list, as the key was not present
     * }
     * </pre>
     *
     * <p>It's important to note that if the key is already present in the table, the existing list will not be modified
     * and the new value will not be set. If you want to update the existing list regardless of its presence, you should
     * use the {@link #setList(String, List)} method instead.</p>
     *
     * @param key   the key associated with the list
     * @param value the new value to be set for the list
     * @return the SavableList associated with the key, representing either the existing list or the newly set list
     * @throws NullPointerException if the specified key is `null`
     * @see #setList(String, List)
     * @see #putListIfAbsent(String, List)
     */
    @NotNull SavableList setListIfAbsent(@NotNull String key, @NotNull List<String> value);

    /**
     * Adds a value to the list associated with the specified key. The modified list will be saved permanently in the
     * table.
     *
     * <p>The `setListValue()` method allows you to add a value to the list associated with the provided key. The
     * modified
     * list will be saved permanently in the table, ensuring that the changes are persisted.</p>
     *
     * <p>The specified key represents the key associated with the list. If the key is not present in the table, a new
     * list will be created with the specified value and associated with the key. If the key already exists in the
     * table,
     * the value will be added to the existing list associated with the key.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * SavableMap savableMap = new SomeSavableMapImplementation();
     * List<String> existingList = Arrays.asList("item1", "item2");
     * savableMap.putList("key1", existingList);
     *
     * SavableList updatedList = savableMap.setListValue("key1", "item3");
     * System.out.println(updatedList); // Output: ["item1", "item2", "item3"], the updated list with the added value
     *
     * SavableList newList = savableMap.setListValue("key2", "item1");
     * System.out.println(newList); // Output: ["item1"], a new list with the added value, as the key was not present
     * }
     * </pre>
     *
     * @param key   the key associated with the list
     * @param value the value to be added to the list
     * @return the SavableList associated with the key, representing the modified list
     * @throws NullPointerException if the specified key or value is `null`
     * @see #putListValue(String, String)
     */
    @NotNull SavableList setListValue(@NotNull String key, @NotNull String value);


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
     * @throws NullPointerException if the specified key is `null`
     * @see #deleteList(String)
     */
    void removeList(@NotNull String key);

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
     * list and also save the changes permanently, you should use the {@link #deleteListValue(String, String)} method
     * instead.</p>
     *
     * @param key   the key associated with the list
     * @param value the value to remove from the list
     * @throws NullPointerException if the specified key or value is `null`
     * @see #deleteListValue(String, String)
     */
    void removeListValue(@NotNull String key, @NotNull String value);

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
     * @see #removeListValue(String, String)
     */
    void deleteListValue(@NotNull String key, @NotNull String value);

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
    void deleteList(@NotNull String key);

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
