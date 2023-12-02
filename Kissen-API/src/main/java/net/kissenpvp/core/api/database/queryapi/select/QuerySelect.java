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

package net.kissenpvp.core.api.database.queryapi.select;

import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.QueryComponent;
import net.kissenpvp.core.api.database.queryapi.RootQueryComponent;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code QuerySelect} interface provides a contract for creating and executing
 * select queries. It extends the {@link QueryComponent} interface, with {@code QuerySelect}
 * as its generic parameter to allow for method chaining.
 *
 * <p>Note: The exact interpretation and execution of these methods may depend on the
 * specific database technology in use.
 */
public interface QuerySelect extends QueryComponent<QuerySelect> {
    /**
     * The {@code QuerySelect} interface provides a contract for creating and executing
     * select queries. It extends the {@link QueryComponent} interface, with {@code QuerySelect}
     * as its generic parameter to allow for method chaining.
     *
     * <p>Note: The exact interpretation and execution of these methods may depend on the
     * specific database technology in use.
     */
    @NotNull Column[] getColumns();

    /**
     * Executes the select query that has been built and returns the result.
     *
     * @return A 2D array of {@link String}, where each inner array represents rows of the result.
     */
    @NotNull String[][] execute();

    /**
     * The {@code RootQuerySelect} interface provides a contract for the root of a select query.
     * It extends the {@link RootQueryComponent} interface with {@code QuerySelect} as its
     * generic parameter.
     *
     * <p>Note: The exact interpretation and execution of these methods may depend on the
     * specific database technology in use.
     */
    interface RootQuerySelect extends RootQueryComponent<QuerySelect> {
        /**
         * Executes the root select query that has been built and returns the result.
         *
         * @return A 2D array of {@link String}, where each inner array represents rows of the result.
         */
        @NotNull String[][] execute();
    }

}
