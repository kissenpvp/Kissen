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

package net.kissenpvp.core.api.database.connection;

import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.Table;
import org.jetbrains.annotations.NotNull;

public interface DatabaseConnection {

    @NotNull String getConnectionID();

    @NotNull String getConnectionString();

    @NotNull DatabaseDriver getDriver();

    boolean isConnected();

    void connect() throws BackendException;

    void disconnect() throws BackendException;

    @NotNull
    Table createTable(@NotNull String table, @NotNull String idColumn, @NotNull String keyColumn, @NotNull String pluginColumn, @NotNull String typeColumn, @NotNull String valueColumn);

    @NotNull
    Table createTable(@NotNull String table);
}
