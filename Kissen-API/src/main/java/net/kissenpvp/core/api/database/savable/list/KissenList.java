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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * The KissenList interface extends the functionality of the List interface and represents a specialized list
 * structure that provides additional features and behaviors.
 * It is a generic interface, allowing the storage and manipulation of elements of any specified type, denoted by the
 * type parameter T.
 *
 * <p>
 * KissenList combines the core functionality of a list with specialized operations and behavior specific to the
 * implementation.
 * It provides methods for adding, removing, accessing, and manipulating elements in the list, following the contract
 * defined by the List interface.
 * </p>
 *
 * <p>
 * This interface extends the List interface, inheriting its standard methods such as size(), isEmpty(), contains(),
 * iterator(), and others.
 * Additionally, KissenList introduces its own specialized methods to enhance the list functionality.
 * </p>
 *
 * <p>
 * The KissenList interface offers flexibility and customization by allowing different implementations to define
 * their own behavior for the specialized operations.
 * It serves as a foundation for creating list structures tailored to specific use cases and requirements.
 * </p>
 *
 * <p>
 * The KissenList interface supports various operations such as inverting the presence of a value, replacing values
 * based on a predicate, and clearing the list and adding multiple elements simultaneously.
 * These operations provide extended capabilities beyond the basic list operations, enabling more advanced
 * functionality.
 * </p>
 *
 * <p>
 * Implementations of KissenList should adhere to the contract specified by the List interface, ensuring
 * compatibility with existing list-based operations.
 * They should provide efficient and effective implementations of the specialized operations defined in this interface.
 * </p>
 *
 * <p>
 * KissenList implementations can be used as drop-in replacements for List in scenarios where the additional
 * functionality is required.
 * They can seamlessly integrate with existing code that relies on the List interface while offering extended
 * capabilities.
 * </p>
 *
 * <p>
 * The KissenList interface promotes code reuse, modularity, and extensibility by defining a consistent set of
 * methods and behaviors for specialized lists.
 * It allows developers to work with lists in a more expressive and powerful way, accommodating specific needs and
 * enabling more efficient operations.
 * </p>
 *
 * <p>
 * Note that KissenList is an interface, and its functionality should be implemented by concrete classes that provide
 * the actual behavior and storage of elements.
 * </p>
 *
 * @param <T> the type of elements stored in the list
 * @see List
 * @see Collection
 */
public interface KissenList<T> extends List<T>
{

    /**
     * Inverts the presence of a value in the list.
     * This means that if the value is present in the list, it will be removed and the method will return an array {
     * false, {@link #remove(Object)}}.
     * If the value is not present in the list, it will be added and the method will return an array {true,
     * {@link #add(Object)}}.
     *
     * @param value the value to invert in the list
     * @return an array of booleans representing the result of the inversion operation. The first element indicates
     * whether the value is present in the list after the operation.
     * The second element indicates whether the inversion action could be executed. If it is false, it means
     * that the operation failed due to some constraints or limitations.
     * @throws ClassCastException       if the class of the specified value prevents it from being added or removed from
     *                                  this list
     * @throws NullPointerException     if the specified value is null and this list does not permit null elements
     * @throws IllegalArgumentException if some property of the specified value prevents it from being added or
     *                                  removed from this list
     * @throws IllegalStateException    if the value cannot be added or removed at this time due to some internal state
     *                                  of the list
     * @see #add(Object)
     * @see #remove(Object)
     * @see List#contains(Object)
     * @see List#add(Object)
     * @see List#remove(Object)
     * @see List#addAll(Collection)
     * @see List#removeAll(Collection)
     * @see List#retainAll(Collection)
     */
    boolean[] invert(@NotNull T value);

    /**
     * Replaces values in the list based on the specified predicate.
     *
     * <p>
     * The method iterates over each element in the list and applies the given predicate to determine whether the
     * element should be replaced.
     * If the predicate evaluates to true for an element, it will be replaced with the specified object.
     * </p>
     *
     * <p>
     * The method returns the number of replacements that were made in the list.
     * If no replacements were made, the method will return 0.
     * </p>
     *
     * <p>
     * Note that the predicate and object parameters must not be null.
     * If either of them is null, a NullPointerException will be thrown.
     * </p>
     *
     * @param predicate the predicate used to determine whether an element should be replaced
     * @param object    the object to replace the matching elements with
     * @return the number of replacements made in the list
     * @throws NullPointerException     if the predicate or object is null
     * @throws ClassCastException       if the class of the specified object prevents it from being added to the list
     * @throws IllegalArgumentException if some property of the specified object prevents it from being added to the
     *                                  list
     * @throws IllegalStateException    if the object cannot be added or replaced at this time due to some internal
     *                                  state of the list
     * @see Predicate
     * @see List#contains(Object)
     * @see List#add(Object)
     * @see List#set(int, Object)
     */
    int replace(@NotNull Predicate<T> predicate, @NotNull T object);

    /**
     * Clears the current elements in the list and adds all elements from the specified collection.
     *
     * <p>
     * This method removes all elements from the list, making it empty, and then adds all elements from the specified
     * collection to the list.
     * The elements will be added in the iteration order of the collection's iterator.
     * </p>
     *
     * <p>
     * Note that the elements collection must not be null.
     * If it is null, a NullPointerException will be thrown.
     * </p>
     *
     * <p>
     * The behavior of this method is equivalent to calling the {@link #clear()} method followed by the
     * {@link #addAll(Collection)} method,
     * but it provides a more efficient way to perform the combined operation of clearing and adding elements.
     * </p>
     *
     * @param elements the collection of elements to be added to the list after clearing it
     * @throws NullPointerException     if the elements collection is null
     * @throws ClassCastException       if the class of an element in the specified collection prevents it from being
     *                                  added to the list
     * @throws IllegalArgumentException if some property of an element in the specified collection prevents it from
     *                                  being added to the list
     * @throws IllegalStateException    if an element in the specified collection cannot be added at this time due to
     *                                  some internal state of the list
     * @see Collection
     * @see List#clear()
     * @see List#addAll(Collection)
     */
    void clearAndAddAll(@NotNull Collection<T> elements);

    /**
     * Retrieves the optional ListAction to be executed when changing something.
     *
     * <p>
     * This method is used to obtain the optional ListAction that should be executed when a change is made to a list.
     * It provides a way to define and retrieve the specific action associated with a change.
     * </p>
     *
     * <p>
     * If an action is defined, it will be returned as an Optional. If no action is defined, an empty Optional will
     * be returned.
     * </p>
     *
     * @return an Optional containing the ListAction to execute when changing something, or an empty Optional if no
     * action is defined
     * @see ListAction
     * @see Optional
     */
    @NotNull Optional<ListAction> getListAction();

    /**
     * Sets the action that should be executed when changing something in the list.
     *
     * <p>
     * This method allows you to specify the action that will be applied when a change occurs in the list.
     * The provided action will be executed after the change has already taken place.
     * </p>
     *
     * <p>
     * The listAction parameter represents the new action to be set.
     * It can be an implementation of the {@link ListAction} interface, which defines the behavior and logic for the
     * specific action to be executed.
     * If a null value is provided, it indicates that no specific action should be executed when changing something
     * in the list.
     * </p>
     *
     * <p>
     * Note that setting a new list action will replace any previously set action, effectively updating the behavior
     * associated with list changes.
     * </p>
     *
     * <p>
     * It is important to choose a suitable action that matches the desired behavior for list changes.
     * The action should handle the necessary operations or logic required based on the specific change scenario.
     * </p>
     *
     * <p>
     * It is recommended to use implementations of the {@link ListAction} interface to define custom actions and
     * encapsulate the desired behavior.
     * These implementations can be created based on the specific requirements and can contain the necessary logic to
     * be executed when a change occurs.
     * </p>
     *
     * <p>
     * If no specific action is set, the list will not execute any additional action after a change.
     * In such cases, the list behavior will be limited to the default operations provided by the list implementation.
     * </p>
     *
     * @param listAction the new action to apply, or null to indicate no specific action should be executed
     * @see ListAction
     * @see ListExecution
     * @see #getListAction()
     * @see List#add(Object)
     * @see List#remove(Object)
     * @see List#set(int, Object)
     * @see List#clear()
     * @see List#addAll(Collection)
     * @see List#removeAll(Collection)
     * @see List#retainAll(Collection)
     */
    void setListAction(@Nullable ListAction listAction);
}
