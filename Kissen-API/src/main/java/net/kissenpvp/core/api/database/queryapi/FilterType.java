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

/**
 * The {@code FilterType} enum is utilized to define the type of filtering operation to be applied
 * during a query. Each value of this enum represents a different kind of comparison operation
 * for filtering records based on column values.
 *
 * <p> Each {@code FilterType} corresponds to a specific comparison operation:
 *
 * <ul>
 * <li>{@code STARTS_WITH} is used to filter records where the column value begins with a specified string.
 * <li>{@code EXACT_MATCH} is used for filtering records based on an exact match with the specified value.
 * <li>{@code ENDS_WITH} is used to filter records where the column value terminates with a specified string.
 * </ul>
 *
 * <p> The specific behavior of these comparison operations might depend on the particularities of the
 * database technology in use and the way the query is interpreted.
 */
public enum FilterType {
    STARTS_WITH, EXACT_MATCH, ENDS_WITH
}
