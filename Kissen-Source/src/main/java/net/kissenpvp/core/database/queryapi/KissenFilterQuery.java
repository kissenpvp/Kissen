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

package net.kissenpvp.core.database.queryapi;

import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.FilterOperator;
import net.kissenpvp.core.api.database.queryapi.FilterQuery;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record KissenFilterQuery(@NotNull Column column, @NotNull Object value, @NotNull FilterOperator filterOperator) implements FilterQuery
{
    public KissenFilterQuery {
        if(!Objects.equals(column, Column.VALUE) && !(value instanceof String))
        {
            String exceptionMessage = "Only String values are allowed for column %s.";
            throw new IllegalArgumentException(String.format(exceptionMessage, column));
        }
    }

    @Override public @NotNull Column getColumn()
    {
        return column;
    }

    @Override public @NotNull Object getValue()
    {
        return value;
    }

    @Override
    public @NotNull FilterOperator getFilterOperator() {
        return filterOperator;
    }
}
