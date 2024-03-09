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
package net.kissenpvp.core.api.database.meta.list;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.Serializable;
import java.util.List;

@FunctionalInterface
public interface ListAction<T> extends Serializable
{
    void execute(@NotNull ListExecutionType listExecution, @NotNull @Unmodifiable List<T> before, @NotNull @Unmodifiable List<T> updatedList);

    enum ListExecutionType implements Serializable
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
}
