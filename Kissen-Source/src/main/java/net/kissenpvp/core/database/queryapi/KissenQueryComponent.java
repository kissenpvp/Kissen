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

import net.kissenpvp.core.api.database.queryapi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class KissenQueryComponent<T extends QueryComponent<?>> implements QueryComponent<T>
{
    private final List<FilterQuery> filterQueries;

    public KissenQueryComponent()
    {
        this.filterQueries = new ArrayList<>();
    }

    @Override public FilterQuery[] getFilterQueries()
    {
        return filterQueries.toArray(new FilterQuery[0]);
    }

    @Override
    public @NotNull T or(@NotNull Column column, @NotNull Object value) {
        filterQueries.add(new KissenFilterQuery(column, value, FilterOperator.OR));
        return (T) this;
    }

    @Override
    public @NotNull T orExact(@NotNull Column column, @NotNull String value) {
        return or(column, String.format("\\b%s\\b", value));
    }

    @Override
    public @NotNull T and(@NotNull Column column, @NotNull Object value) {
        filterQueries.add(new KissenFilterQuery(column, value, FilterOperator.AND));
        return (T) this;
    }

    @Override
    public @NotNull T andExact(@NotNull Column column, @NotNull String value) {
        return and(column, String.format("\\b%s\\b", value));
    }

    public @NotNull T initialise(@NotNull Column column, @NotNull Object value)
    {
        filterQueries.add(new KissenFilterQuery(column, value, FilterOperator.INIT));
        return (T) this;
    }
}
