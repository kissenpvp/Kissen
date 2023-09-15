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

package net.kissenpvp.core.database.settings;

import net.kissenpvp.core.api.config.options.OptionString;
import org.jetbrains.annotations.NotNull;


public class DatabaseDNS extends OptionString {
    @Override
    public @NotNull String getGroup() {
        return "table";
    }

    @Override
    public @NotNull String getDescription() {
        return "This option allows you to specify the database connection URL for your application.";
    }

    @Override
    public @NotNull String getDefault() {
        return "jdbc:sqlite:kissen.db";
    }

    @Override
    public int getPriority() {
        return 6;
    }
}
