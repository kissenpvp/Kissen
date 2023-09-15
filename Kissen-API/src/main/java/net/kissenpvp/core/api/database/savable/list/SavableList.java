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

package net.kissenpvp.core.api.database.savable.list;

import net.kissenpvp.core.api.database.meta.Meta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The SavableList interface extends the functionality of the KissenList interface and represents a specialized list
 * structure that provides the ability to interact with a database.
 * It is a generic interface that specifically works with elements of type String, as indicated by the type parameter
 * in the KissenList interface extension.
 *
 * <p>
 * SavableList serves as a direct interface to a database and allows for seamless integration between the list
 * operations and the corresponding database operations.
 * Changes made to the list directly affect the associated database, ensuring data consistency between the list and
 * the database.
 * </p>
 *
 * <p>
 * The SavableList interface provides additional methods beyond those inherited from the KissenList interface.
 * One of these methods is the toRecordList() method, which allows converting the list elements into record objects
 * of a specified type using JSON serialization.
 * The toRecordList() method takes a Class parameter representing the target record type and returns a
 * SavableRecordList containing the converted records.
 * </p>
 *
 * <p>
 * The SavableList interface provides a convenient way to work with lists of String elements that are synchronized
 * with a database.
 * It enables operations such as adding, removing, updating, and querying the list while ensuring the corresponding
 * database records are properly modified.
 * </p>
 *
 * <p>
 * Implementations of the SavableList interface should provide concrete implementations for the methods inherited
 * from the KissenList and List interfaces.
 * Additionally, they should handle the synchronization of list changes with the corresponding database operations.
 * </p>
 *
 * <p>
 * The SavableList interface is typically used in scenarios where a list of String elements needs to be stored and
 * managed in a database.
 * It is especially useful when there is a requirement for real-time synchronization between the list and the
 * database records.
 * </p>
 *
 * <p>
 * Note that the SavableList interface does not prescribe or enforce a specific database implementation or technology.
 * It uses the {@link Meta} in order to interact with database directly.
 * </p>
 *
 * @see KissenList
 * @see SavableRecordList
 * @see List
 * @see java.util.Collection
 * @see Meta
 */
public interface SavableList extends KissenList<String> {
    /**
     * Converts the elements of the list into record objects of the specified type using JSON serialization.
     * Each list element is transformed into a record object based on the provided target record class.
     * The resulting record objects are then returned in a SavableRecordList.
     *
     * <p>
     * The toRecordList() method allows for the convenient conversion of the list elements to record objects,
     * facilitating the storage, manipulation, and retrieval of structured data in a database.
     * </p>
     *
     * <p>
     * The method takes a Class parameter representing the target record type.
     * This allows for specifying the desired structure and behavior of the resulting record objects.
     * </p>
     *
     * <p>
     * The target record class should extend the Record interface or implement the necessary serialization and
     * deserialization logic.
     * It should provide the required fields and methods to represent the data structure of the record.
     * </p>
     *
     * <p>
     * The toRecordList() method returns a SavableRecordList, which is a specialized list structure designed to work
     * with record objects.
     * The SavableRecordList maintains the link between the list elements and the associated database records,
     * ensuring that modifications to the list are reflected in the corresponding database operations.
     * </p>
     *
     * <p>
     * It is important to note that the conversion process relies on JSON serialization.
     * Each list element is transformed into a JSON representation and then deserialized into a record object of the
     * specified type.
     * Therefore, the target record class should properly handle JSON serialization and deserialization to ensure
     * accurate data transformation.
     * </p>
     *
     * <p>
     * The toRecordList() method provides a powerful and flexible way to work with structured data stored as String
     * elements in the list.
     * By converting the list elements into record objects, developers can leverage the advantages of structured data
     * storage and retrieval
     * while maintaining the convenience and flexibility of working with lists.
     * </p>
     *
     * @param <T>    the type of the record object that the list elements will be converted to
     * @param record the Class representing the target record type
     * @return a SavableRecordList containing the record objects converted from the list elements
     * @see SavableRecordList
     * @see Record
     * @see java.util.Collection
     */
    <T extends Record> @NotNull SavableRecordList<T> toRecordList(@NotNull Class<T> record);
}
