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

package net.kissenpvp.core.api.database;

import net.kissenpvp.core.api.base.Implementation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * The StorageImplementation interface represents a general cache system that provides maps behind specified keys.
 * It serves as a component for storing and retrieving data based on unique IDs.
 *
 * <p>The StorageImplementation interface offers methods for retrieving stored content, dropping data from specific IDs,
 * checking the existence of cache objects, and performing cache object operations such as deletion, writing, and
 * reading.
 *
 * <p>Implementations of StorageImplementation are responsible for handling the underlying storage mechanisms and
 * managing the cached data. They should ensure thread-safety and handle any necessary synchronization to
 * support concurrent access to the cache data.
 * They should also enforce data integrity and apply appropriate error handling mechanisms when interacting with the
 * underlying storage system.
 * They can use various storage mechanisms like in-memory caches, file systems, databases, or other forms of storage.
 * StorageImplementation provides a flexible and generic way to store and retrieve data, abstracting away the storage
 * details.
 *
 * <p>It is important to note that StorageImplementation is a system component focused on caching and managing data
 * based on unique IDs.
 * It is not specific to any particular domain or object type, providing a generic cache system that can be used in
 * various contexts.
 *
 * <p>Also note that this is not meant for permanent data. The cache will be cleared when the
 * {@link #dropStorage(String)} method is called or when the server stops.
 */
public interface StorageImplementation extends Implementation {

    /**
     * Retrieves the stored content based on the specified ID.
     *
     * <p>The id parameter represents the unique identifier of the stored content to retrieve.
     * If the specified ID exists in the cache, the method returns a map containing the previously saved data.
     * If the ID does not exist in the cache, an empty map will be returned.
     *
     * @param id the ID of the content to retrieve.
     * @return a map containing the previously saved data, or an empty map if the ID does not exist.
     */
    @NotNull Map<String, Object> getStorage(@NotNull String id);

    /**
     * Drops the stored data associated with the specified ID.
     *
     * <p>The id parameter represents the ID of the object for which the stored data will be dropped.
     * This method is typically called in the delete() method of objects to clear the temporary storage.
     * When this method is called, the data associated with the ID should be removed from the cache.
     *
     * @param id the ID of the object to clear.
     */
    void dropStorage(@NotNull String id);

    /**
     * Checks if a cache object exists for the specified key.
     *
     * <p>The containsCacheObject method checks whether a cache object exists in the cache for the specified key.
     * If a cache object exists for the key, it returns true; otherwise, it returns false.
     *
     * @param key the key to check.
     *            It should be a non-null String representing the key used to identify the cache object in the cache.
     *            Examples of valid keys can be "user:123", "data:456", etc.
     * @return true if a cache object exists for the key, false otherwise.
     */
    boolean containsCacheObject(@NotNull String key);

    /**
     * Deletes the cache object associated with the specified key.
     *
     * <p>The deleteCacheObject method deletes the cache object from the cache that is associated with the specified
     * key.
     * If the cache object is successfully deleted, it returns true; otherwise, it returns false.
     * If an I/O error occurs during the deletion process, an IOException is thrown.
     *
     * @param key the key of the cache object to delete.
     *            It should be a non-null String representing the key used to identify the cache object in the cache.
     *            Examples of valid keys can be "user:123", "data:456", etc.
     * @return true if the cache object was successfully deleted, false otherwise.
     * @throws IOException if an I/O error occurs during the deletion process.
     *                     It is thrown when an I/O error occurs while deleting the cache object from the cache.
     */
    boolean deleteCacheObject(@NotNull String key) throws IOException;

    /**
     * Writes a cache object with the specified key and serializable data.
     *
     * <p>The writeCacheObject method stores the provided serializable data in the cache as a cache object with the
     * specified key.
     * The cache object can later be retrieved using the same key.
     * If the serializable data is null, an IllegalArgumentException is thrown.
     * If an I/O error occurs during the writing process, an IOException is thrown.
     *
     * @param key          the key of the cache object to write.
     *                     It should be a non-null String representing the key used to identify the cache object in
     *                     the cache.
     *                     Examples of valid keys can be "user:123", "data:456", etc.
     * @param serializable the serializable data to be stored in the cache object.
     *                     It represents the data that will be associated with the cache object.
     * @throws IllegalArgumentException if the serializable data is null.
     *                                  It is thrown when the provided serializable data is null.
     * @throws IOException              if an I/O error occurs during the writing process.
     *                                  It is thrown when an I/O error occurs while writing the cache object to the
     *                                  cache.
     */
    <T extends Serializable> void writeCacheObject(@NotNull String key, @Nullable T serializable) throws IllegalArgumentException, IOException;

    /**
     * Reads a cache object with the specified key and returns it as an instance of the specified class.
     *
     * <p>The readCacheObject method retrieves a cache object from the cache based on the provided key.
     * It then attempts to cast the cache object to the specified class type.
     * If the cache object can be successfully cast to the specified class, an instance of the cache object is returned.
     *
     * @param key   the key of the cache object to read.
     *              It should be a non-null String representing the key used to identify the cache object in the cache.
     *              Examples of valid keys can be "user:123", "data:456", etc.
     * @param clazz the class representing the type of the cache object.
     *              It should be a non-null Class representing the expected class type of the cache object.
     *              Example: MyClass.class
     * @return an instance of the cache object with the specified key and class.
     * @throws ClassCastException       if the cache object cannot be cast to the specified class.
     *                                  It is thrown when the retrieved cache object is not of the expected class type.
     * @throws NullPointerException     if the cache object is null.
     *                                  It is thrown when the retrieved cache object is null.
     * @throws IllegalArgumentException if the specified class is null.
     *                                  It is thrown when the specified class is null.
     */
    <T extends Serializable> @NotNull T readCacheObject(@NotNull String key, @NotNull Class<T> clazz) throws ClassCastException, NullPointerException;

}
