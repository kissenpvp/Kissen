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

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * This interface defines an action to be executed when a data change has already happened in a list.
 * It extends the Serializable interface, allowing implementations to be serialized and deserialized.
 *
 * <p>
 * Implementations of this interface provide a method called "execute" which specifies the action to be performed.
 * The "execute" method takes a ListExecution object as a parameter, representing the execution that will be executed,
 * and accepts additional values that are specific to the implemented action.
 * </p>
 *
 * <p>
 * Note that the execute method does not return any value.
 * It is meant to perform the necessary actions based on the provided execution and values.
 * </p>
 *
 * <p>
 * Implementations of this interface should specify the behavior and logic for the specific action to be executed.
 * They can define any required parameters and values, and perform the necessary operations accordingly.
 * </p>
 *
 * <p>
 * The ListAction interface is designed to be used in the context of changing a list's data,
 * where an action needs to be performed after the change has already occurred.
 * It provides a flexible way to define and execute different actions based on the specific needs and requirements.
 * </p>
 *
 * @see Serializable
 * @see ListExecution
 */
public interface ListAction extends Serializable
{
    /**
     * Executes the action when a data change has already happened.
     *
     * <p>
     * This method is responsible for performing the necessary actions based on the provided execution and values.
     * The execution represents the specific execution that will be executed, and the values parameter allows passing
     * additional values
     * that are specific to the implemented action.
     * </p>
     *
     * <p>
     * Note that the execute method does not return any value. It is meant to perform the necessary operations
     * without a specific return.
     * </p>
     *
     * @param listExecution the execution which will be executed
     * @param values        the values that are specific to the implemented action
     * @see ListExecution
     */
    void execute(@NotNull ListExecution listExecution, @NotNull Object... values);
}
