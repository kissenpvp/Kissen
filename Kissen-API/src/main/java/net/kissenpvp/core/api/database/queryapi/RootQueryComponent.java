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

package net.kissenpvp.core.api.database.queryapi;

import org.jetbrains.annotations.NotNull;

/**
 * The RootQueryComponent interface expands upon the {@link QueryComponent} interface by
 * providing functionality of a root query. This interface is intended for use with types
 * of queries that can be considered the start or root of a query chain.
 *
 * <p> The RootQueryComponent interface is a generic interface, following the same design
 * as QueryComponent, and its generic parameter T represents the type of the implementing
 * class, which must also be a type of QueryComponent. This design allows the interface
 * to initiate a fluent method chain.
 *
 * @param <T> The type of the implementing class. This type will be returned when chaining methods together.
 * @see QueryComponent
 */
public interface RootQueryComponent<T extends QueryComponent<?>> {
    /**
     * This method applies a WHERE filter condition to the implementing class. This method
     * is primarily expected to be the first method in a method chain, aligning with the SQL
     * standard where a query generally starts with a WHERE clause.
     *
     * @param column     The column to apply the WHERE condition on.
     * @param value      The value for the column to be compared against.
     * @param filterType The comparison type to perform on the pair, column-value.
     * @return The implementing class, to enable method chaining.
     */
    @NotNull T where(@NotNull Column column, @NotNull String value, @NotNull FilterType filterType);
}
