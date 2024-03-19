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

import java.sql.SQLException;
import java.util.Objects;
import java.util.regex.Pattern;

public class KissenSQLiteDatabaseConnection extends KissenJDBCDatabaseConnection {

    public KissenSQLiteDatabaseConnection(@NotNull String connectionID, @NotNull String connectionString) {
        super(connectionID, connectionString, DatabaseDriver.SQLITE);
    }

    @Override
    public void connect() throws BackendException {
        if (!getConnectionString().toLowerCase().startsWith("jdbc:sqlite:")) {
            throw new BackendException(new IllegalStateException("Illegal connection string given to sqlite connection"));
        }
        super.connect();

        registerRegexFunction();
    }

    private void registerRegexFunction() throws BackendException {
        try {
            org.sqlite.Function.create(getConnection(), "REGEXP", new org.sqlite.Function() {
                @Override
                public void xFunc() throws SQLException {
                    String expression = value_text(0);
                    String value = Objects.requireNonNullElse(value_text(1), "");
                    Pattern pattern = Pattern.compile(expression);
                    result(pattern.matcher(value).find() ? 1 : 0);
                }
            });
        } catch (Exception exception) {
            throw new BackendException(exception);
        }
    }
}
