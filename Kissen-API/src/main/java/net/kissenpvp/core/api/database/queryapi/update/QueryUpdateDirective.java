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

package net.kissenpvp.core.api.database.queryapi.update;

import net.kissenpvp.core.api.database.queryapi.Column;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code QueryUpdateDirective} record represents a simple data holder for a column to-be-updated
 * and its new value within an update query. It consists of a {@link Column} and the corresponding
 * value as a {@code String}. The {@code QueryUpdateDirective} allows for easy specification and handling
 * of update operations for columns in a query.
 *
 * <p> Note: The {@link Column} and the value are annotated with {@code @NotNull} to indicate that they
 * cannot be {@code null}, and a valid {@code QueryUpdateDirective} should always have these fields
 * populated.
 *
 * @param column The {@link Column} object representing the column to be updated.
 * @param value  The new value for the column in {@code String} format.
 */
public record QueryUpdateDirective(@NotNull Column column, @NotNull String value) {
}
