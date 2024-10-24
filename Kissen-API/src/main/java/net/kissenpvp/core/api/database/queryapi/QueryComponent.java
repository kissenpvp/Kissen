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
 * The QueryComponent interface defines contract for creating query components
 * and chaining them together to generate complex queries. A QueryComponent
 * can be a query, a filter, a sort condition etc. It is designed to be flexible
 * and applicable across various types of queries.
 *
 * <p> The QueryComponent interface is a generic interface and thus, can be used
 * to create strict type-safe query components. The generic parameter {@code T} represents
 * the type of the implementing class, and it extends QueryComponent interface.
 * This enables a fluent interface for method chaining.
 *
 * @param <T> The type of the implementing class. This type will be returned when
 *            chaining methods together.
 */
public interface QueryComponent<T extends QueryComponent<?>> {

    /**
     * This method gets an array of {@link FilterQuery} objects that are associated with the
     * implementing class. This method should be overridden to return an appropriate array
     * of FilterQuery objects.
     *
     * @return an array of FilterQuery objects.
     */
    @NotNull FilterQuery[] getFilterQueries();

    @NotNull T or(@NotNull Column column, @NotNull Object value);

    @NotNull
    T orExact(@NotNull Column column, @NotNull String value);

    @NotNull T and(@NotNull Column column, @NotNull Object value);

    @NotNull
    T andExact(@NotNull Column column, @NotNull String value);
}
