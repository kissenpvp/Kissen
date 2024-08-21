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

package net.kissenpvp.core.database.jdbc;

import net.kissenpvp.core.api.database.connection.DatabaseDriver;
import net.kissenpvp.core.api.database.meta.BackendException;
import org.jetbrains.annotations.NotNull;

public class KissenMySQLDatabaseConnection extends KissenJDBCDatabaseConnection {
    public KissenMySQLDatabaseConnection(@NotNull String connectionID, @NotNull String connectionString) {
        super(connectionID, connectionString, DatabaseDriver.MYSQL);
    }

    @Override
    public void connect() throws BackendException {
        if (!getConnectionString().toLowerCase().startsWith("jdbc:mysql://")) {
            throw new BackendException(new IllegalStateException("Illegal connection string given to mysql connection"));
        }
        super.connect();
    }
}
