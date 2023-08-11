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

package net.kissenpvp.core.api.database.savable.list;

import net.kissenpvp.core.api.database.meta.Meta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * The SavableRecordList interface extends the functionality of the SavableList interface and represents a
 * specialized list structure specifically designed to work with record objects.
 * It is a generic interface that works with elements of type T, where T is constrained to be a subclass of the
 * Record interface.
 *
 * <p>
 * SavableRecordList serves as a direct interface to a database and provides seamless integration between the list
 * operations and the corresponding database operations.
 * It extends the capabilities of the SavableList interface by introducing methods specifically tailored for working
 * with record objects.
 * </p>
 *
 * <p>
 * The SavableRecordList interface includes methods for adding, removing, replacing, and querying record objects
 * within the list.
 * These methods ensure proper synchronization between the list and the associated database records, maintaining data
 * integrity and consistency.
 * </p>
 *
 * <p>
 * The SavableRecordList interface inherits the toRecordList() method from the SavableList interface.
 * This method allows converting the list elements to a read-only List of record objects, providing a convenient way
 * to work with the records independently of the underlying list structure.
 * </p>
 *
 * <p>
 * The SavableRecordList interface assumes a direct connection to a database and expects proper database
 * configuration and setup.
 * Any modifications made to the list will directly affect the corresponding database tables and records.
 * </p>
 *
 * <p>
 * Implementations of the SavableRecordList interface should provide concrete implementations for the methods
 * inherited from the SavableList and KissenList interfaces.
 * Additionally, they should handle the synchronization of list changes with the corresponding database operations
 * specific to record objects.
 * </p>
 *
 * <p>
 * The SavableRecordList interface is typically used in scenarios where a list of record objects needs to be stored
 * and managed in a database.
 * It facilitates seamless integration between the list operations and the corresponding database operations,
 * ensuring data consistency and synchronization.
 * </p>
 *
 * <p>
 * Note that the SavableRecordList interface does not prescribe or enforce a specific database implementation or
 * technology.
 * It can be used with the {@link Meta} implementation the system provides.
 * </p>
 *
 * @param <T> the type of record object that the list elements can represent
 * @see SavableList
 * @see Record
 * @see List
 * @see Collection
 */
public interface SavableRecordList<T extends Record> extends SavableList {
    /**
     * Adds the specified record element to the list.
     * The element is added at the end of the list.
     *
     * <p>
     * The add() method ensures that the list and the associated database are synchronized, adding the record element
     * to both the list and the database.
     * It returns true if the addition is successful and false otherwise.
     * </p>
     *
     * @param element the record element to add to the list
     * @return true if the element is successfully added, false otherwise
     * @see Record
     * @see #addAllRecord(Collection)
     */
    boolean add(@NotNull T element);

    /**
     * Inserts the specified record element at the specified index in the list.
     * Any existing elements at or after the specified index are shifted to the right.
     *
     * <p>
     * The add() method ensures that the list and the associated database are synchronized, inserting the record
     * element at the specified index in both the list and the database.
     * </p>
     *
     * @param index   the index at which to insert the element
     * @param element the record element to insert into the list
     * @see Record
     * @see #add(Record)
     */
    void add(int index, @NotNull T element);

    /**
     * Adds all the record elements in the specified collection to the end of the list.
     *
     * <p>
     * The addAllRecord() method ensures that the list and the associated database are synchronized, adding all the
     * record elements to both the list and the database.
     * It returns true if the list is modified as a result of the operation.
     * </p>
     *
     * @param elements the collection of record elements to add to the list
     * @return true if the list is modified, false otherwise
     * @see Record
     * @see #add(Record)
     */
    boolean addAllRecord(@NotNull Collection<T> elements);

    /**
     * Inserts all the record elements in the specified collection at the specified index in the list.
     * Any existing elements at or after the specified index are shifted to the right.
     *
     * <p>
     * The addAllRecord() method ensures that the list and the associated database are synchronized, inserting all
     * the record elements at the specified index in both the list and the database.
     * It returns true if the list is modified as a result of the operation.
     * </p>
     *
     * @param index    the index at which to insert the elements
     * @param elements the collection of record elements to insert into the list
     * @return true if the list is modified, false otherwise
     * @see Record
     * @see #add(int, Record)
     */
    boolean addAllRecord(int index, @NotNull Collection<T> elements);

    /**
     * Removes the specified record element from the list.
     *
     * <p>
     * The remove() method ensures that the list and the associated database are synchronized, removing the record
     * element from both the list and the database.
     * It returns true if the element is successfully removed, false otherwise.
     * </p>
     *
     * @param element the record element to remove from the list
     * @return true if the element is successfully removed, false otherwise
     * @see Record
     * @see #removeAllRecord(Collection)
     */
    boolean remove(@NotNull T element);

    /**
     * Removes all the record elements in the specified collection from the list.
     *
     * <p>
     * The removeAllRecord() method ensures that the list and the associated database are synchronized, removing all
     * the record elements from both the list and the database.
     * It returns true if the list is modified as a result of the operation.
     * </p>
     *
     * @param elements the collection of record elements to remove from the list
     * @return true if the list is modified, false otherwise
     * @see Record
     * @see #remove(Record)
     */
    boolean removeAllRecord(@NotNull Collection<T> elements);

    /**
     * Removes all the record elements from the list that match the given predicate.
     *
     * <p>
     * The removeIfRecord() method ensures that the list and the associated database are synchronized, removing all
     * the record elements that satisfy the given predicate from both the list and the database.
     * It returns true if the list is modified as a result of the operation.
     * </p>
     *
     * @param filter the predicate used to match the record elements to be removed
     * @return true if the list is modified, false otherwise
     * @see Record
     */
    boolean removeIfRecord(@NotNull Predicate<? super T> filter);

    /**
     * Replaces all the record elements in the list that match the given predicate with the specified element.
     *
     * <p>
     * The replaceRecord() method ensures that the list and the associated database are synchronized, replacing all
     * the record elements that satisfy the given predicate with the specified element in both the list and the
     * database.
     * It returns the number of replacements made.
     * </p>
     *
     * @param predicate the predicate used to match the record elements to be replaced
     * @param element   the element to replace the matching record elements with
     * @return the number of replacements made
     * @see Record
     */
    int replaceRecord(@NotNull Predicate<T> predicate, @NotNull T element);

    /**
     * Checks if the list contains the specified record element.
     *
     * @param element the record element to check for presence in the list
     * @return true if the list contains the element, false otherwise
     * @see Record
     */
    boolean contains(@NotNull T element);

    /**
     * Converts the SavableRecordList to a read-only List of record objects.
     * The returned list provides a convenient way to work with the record objects independently of the underlying
     * list structure.
     *
     * <p>
     * The toRecordList() method returns a read-only List of record objects.
     * It ensures that modifications made to the list do not affect the returned list, preserving the integrity of
     * the record objects.
     * </p>
     *
     * @return a read-only List of record objects
     * @see Record
     * @see List
     */
    @NotNull @Unmodifiable List<T> toRecordList();
}