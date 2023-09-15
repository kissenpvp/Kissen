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

import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.api.database.savable.SavableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.Optional;

/**
 * The ObjectMeta interface represents a system component responsible for managing objects and associated data in a
 * general sense.
 * It serves as an overall interface to the underlying database and provides methods to add and remove objects, as
 * well as retrieve object data.
 * Implementations of this interface define the functionality for interacting with the underlying storage system and
 * managing the object data.
 *
 * <p>ObjectMeta allows adding data to objects identified by a unique ID, removing objects by their total ID, and
 * retrieving data associated with objects.
 * It provides a flexible and generic way to manage objects and their data, independent of specific object types or
 * domains.
 *
 * <p>Implementations of ObjectMeta should handle the underlying storage operations such as storing, retrieving, and
 * deleting object data.
 * They can use various storage mechanisms like databases, file systems, or other forms of persistent storage.
 * ObjectMeta serves as an abstraction layer that encapsulates the storage details and provides a uniform interface
 * for managing objects and their data.
 *
 * <p>Note that ObjectMeta is a system component focused on managing objects and associated data in a general context.
 * It does not contain domain-specific methods or functionalities, as those are typically implemented in more
 * specialized interfaces or classes.
 * ObjectMeta plays a crucial role in managing and organizing the object data, providing a foundation for data
 * management in the system.
 *
 * <p>Implementations of this interface should ensure thread-safety and handle any necessary synchronization to
 * support concurrent access to the object data.
 * Additionally, they should enforce data integrity and apply appropriate error handling mechanisms when interacting
 * with the storage system.
 *
 * @see Meta
 */
public interface ObjectMeta extends Meta {

    /**
     * Adds data to the object with the specified ID.
     *
     * <p>The ID parameter represents the unique identifier of the object to which the data will be added.
     * It is a non-null string that uniquely identifies the object within the system.
     * The data parameter is an optional map containing key-value pairs representing the data to be added to the object.
     * If the data parameter is null or an empty map, no data will be added to the object.
     *
     * @param id   the ID of the object to which the data will be added.
     * @param data a map containing the data to be added to the object.
     */
    void add(@NotNull String id, @NotNull final Map<@NotNull String, @NotNull String> data) throws BackendException;

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
    @NotNull Optional<SavableMap> getData(@NotNull String totalId) throws BackendException;

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
    <T extends Savable> @Unmodifiable @NotNull Map<@NotNull String, @NotNull SavableMap> getData(@NotNull T savable) throws BackendException;
}
