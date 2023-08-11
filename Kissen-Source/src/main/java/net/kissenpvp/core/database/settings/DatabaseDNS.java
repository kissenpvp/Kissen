/*
 * Copyright 2023 KissenPvP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
