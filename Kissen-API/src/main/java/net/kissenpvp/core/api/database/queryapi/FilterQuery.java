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
 * The {@code FilterQuery} interface defines a contract for a filter condition in a query.
 * Implementations of {@code FilterQuery} provide all the necessary details for creating a
 * filter condition against a specified column with a specific value and filter type.
 *
 * <p> It encapsulates the following aspects of a filter condition:
 * <ul>
 * <li> The column to be filtered on.
 * <li> The value to be compared against.
 * <li> The type of filter condition to be applied (such as starts with, exact match, ends with).
 * <li> The operator used to link this filter with other filters (such as AND, OR).
 * </ul>
 */
public interface FilterQuery {

    /**
     * Retrieves the value to be compared in the filter condition.
     * This value is a critical component of a filter condition as
     * it provides the data against which the column data is compared.
     *
     * <p> It can represent a string, number, boolean, or any other
     * data type based on filter requirement. As annotated with the
     * {@code @NotNull}, this method should never return a {@code null}.
     *
     * @return A string representing the value for the filter condition,
     * not {@code null}.
     */
    @NotNull String getValue();

    /**
     * Retrieves the {@link Column} to be filtered in the filter condition.
     * This is the column of the database on which the filtering operation
     * specified by the {@link FilterType} will be performed.
     *
     * <p> Annotated with {@code @NotNull}, this method should never return {@code null}.
     *
     * @return The {@link Column} for the filter condition, not {@code null}.
     */
    @NotNull Column getColumn();

    /**
     * Retrieves the type of filter to be applied as dictated by the {@link FilterType} enum.
     * This determines the type of comparison operation to use in the filter condition.
     *
     * <p> As per {@link FilterType}, it could represent operations like 'Starts With',
     * 'Exact Match' or 'Ends With'. Annotated with {@code @NotNull}, this method should never return {@code null}.
     *
     * @return The {@link FilterType} for the filter condition, not {@code null}.
     */
    @NotNull FilterType getFilterType();

    /**
     * Gets the operator used to join this filter with other filters in a query.
     * The operator is represented by the {@link FilterOperator} enum, which
     * can take values like AND, OR, etc., based on the specific requirements
     * of the query.
     *
     * <p> As annotated with {@code @NotNull}, this method is assured not to
     * return a null value in the context of forming a filter query.
     *
     * @return The {@link FilterOperator} that represents the operator for
     * combining this filter with others in the query, should not be {@code null}.
     */
    @NotNull FilterOperator getFilterOperator();
}
