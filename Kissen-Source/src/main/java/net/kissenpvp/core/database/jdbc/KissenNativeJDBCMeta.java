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

import org.jetbrains.annotations.NotNull;

/**
 * The {@code KissenNativeMeta} class is a concrete implementation of {@link KissenJDBCMeta} that provides native
 * metadata for Kissen objects in the database.
 *
 * <p>
 * This class extends the {@link KissenJDBCMeta} class and adds specific functionality and properties for Kissen native
 * metadata.
 * </p>
 *
 * <p>
 * The class provides a constructor that accepts a table name as a parameter. The table name is used to initialize
 * the superclass
 * {@link KissenJDBCMeta} with the necessary column names: "uuid", "identifier", and "value".
 * </p>
 *
 * <p>
 * This class is typically used to represent metadata for native Kissen objects in the database.
 * </p>
 *
 * @see KissenJDBCMeta
 */
public abstract class KissenNativeJDBCMeta extends KissenJDBCMeta {
    /**
     * Constructs a new {@code KissenNativeMeta} object with the specified table name.
     *
     * <p>
     * This constructor initializes the {@code KissenNativeMeta} object with the given table name and the standard
     * column names
     * required for Kissen native metadata: "uuid", "identifier", and "value".
     * </p>
     *
     * <p>
     * The table name should correspond to the name of the table associated with the Kissen objects in the database.
     * </p>
     *
     * @param table the name of the table associated with the Kissen objects.
     * @throws NullPointerException if the provided table name is {@code null}.
     */
    public KissenNativeJDBCMeta(@NotNull String table) {
        super(table, "uuid", "identifier", "value");
    }
}
