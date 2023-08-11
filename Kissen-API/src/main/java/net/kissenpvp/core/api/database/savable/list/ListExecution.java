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

import java.io.Serializable;
import java.util.List;

/**
 * The ListExecution enum represents different types of list operations or executions that can occur when modifying a
 * list.
 * It provides a predefined set of execution types, each corresponding to a specific list operation.
 * The enum values can be used to identify and differentiate between different execution scenarios.
 * ListExecution implements the Serializable interface, allowing instances of this enum to be serialized and
 * deserialized.
 *
 * <p>
 * The ListExecution enum includes the following execution types:
 * </p>
 *
 * <ul>
 *   <li>SET: Represents the execution type for the "set" operation, which replaces an element at a specific index in
 *   the list.</li>
 *   <li>ADD: Represents the execution type for the "add" operation, which adds an element to the list.</li>
 *   <li>ADD_INDEX: Represents the execution type for the "add" operation with an index, which adds an element at a
 *   specific index in the list.</li>
 *   <li>ADD_ALL: Represents the execution type for the "addAll" operation, which adds multiple elements to the list
 *   .</li>
 *   <li>ADD_ALL_INDEX_INCLUDED: Represents the execution type for the "addAll" operation with index inclusion, which
 *   adds multiple elements to the list, including the specified index.</li>
 *   <li>REMOVE: Represents the execution type for the "remove" operation, which removes a specific element from the
 *   list.</li>
 *   <li>REMOVE_INDEX: Represents the execution type for the "remove" operation with an index, which removes the
 *   element at a specific index in the list.</li>
 *   <li>REMOVE_ALL: Represents the execution type for the "removeAll" operation, which removes multiple elements
 *   from the list based on another collection.</li>
 *   <li>REMOVE_RANGE: Represents the execution type for the "removeRange" operation, which removes elements within a
 *   specified range of indices in the list.</li>
 *   <li>REMOVE_IF: Represents the execution type for the "removeIf" operation, which removes elements from the list
 *   based on a given predicate.</li>
 *   <li>RETAIN_ALL: Represents the execution type for the "retainAll" operation, which retains only the elements in
 *   the list that are also present in another collection.</li>
 *   <li>REPLACE_ALL: Represents the execution type for the "replaceAll" operation, which replaces all occurrences of
 *   a specific element in the list with another element.</li>
 *   <li>REPLACE: Represents the execution type for the "replace" operation, which replaces elements in the list
 *   based on a given predicate.</li>
 *   <li>CLEAR: Represents the execution type for the "clear" operation, which removes all elements from the list,
 *   resulting in an empty list.</li>
 *   <li>UNDEFINED: Represents an undefined or unknown execution type, typically used as a default or fallback value
 *   .</li>
 * </ul>
 *
 * <p>
 * The ListExecution enum can be used to identify the type of execution when handling list modifications or to switch
 * between different execution scenarios.
 * It provides a standardized way to refer to specific operations and allows for better code readability and
 * maintainability.
 * </p>
 *
 * <p>
 * Note that ListExecution is an enum and, as such, represents a fixed set of values that cannot be extended or
 * modified at runtime.
 * </p>
 *
 * @see Serializable
 * @see ListAction
 * @see List
 * @see java.util.Collection
 */
public enum ListExecution implements Serializable
{
    SET,
    ADD,
    ADD_INDEX,
    ADD_ALL,
    ADD_ALL_INDEX_INCLUDED,
    REMOVE,
    REMOVE_INDEX,
    REMOVE_ALL,
    REMOVE_RANGE,
    REMOVE_IF,
    RETAIN_ALL,
    REPLACE_ALL,
    REPLACE,
    CLEAR,
    UNDEFINED
}
