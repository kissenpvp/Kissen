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

import net.kissenpvp.core.api.database.queryapi.QueryComponent;
import net.kissenpvp.core.api.database.queryapi.RootQueryComponent;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code QueryUpdate} interface provides a contract for creating and executing
 * update queries. It extends the {@link QueryComponent} interface, with {@code QueryUpdate}
 * as its generic parameter to allow for method chaining.
 *
 * <p>Note: The exact interpretation and execution of these methods may depend on the
 * specific database technology in use.
 */
public interface QueryUpdate extends QueryComponent<QueryUpdate> {
    /**
     * Gets the columns that are to be updated in the query.
     *
     * @return An array of {@link QueryUpdateDirective}, which represents the columns to be updated.
     */
    @NotNull QueryUpdateDirective[] getColumns();

    /**
     * Executes the update query that has been built.
     *
     * @return A long value indicating the execution result. The interpretation of this
     * result may depend on the specific database technology in use.
     */
    long execute();

    /**
     * The {@code RootQueryUpdate} interface provides a contract for the root of an update query.
     * It extends the {@link RootQueryComponent} interface with {@code QueryUpdate} as its
     * generic parameter.
     *
     * <p>Note: The exact interpretation and execution of these methods may depend on the
     * specific database technology in use.
     */
    interface RootQueryUpdate extends RootQueryComponent<QueryUpdate> {
        /**
         * Executes the root update query that has been built.
         *
         * @return A long value indicating the execution result. The interpretation of this
         * result may depend on the specific database technology in use.
         */
        long execute();
    }
}
