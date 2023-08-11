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

package net.kissenpvp.core.message.localization.settings;

import net.kissenpvp.core.api.config.options.OptionBoolean;
import org.jetbrains.annotations.NotNull;


public class HighlightVariables extends OptionBoolean {

    @Override
    public @NotNull String getGroup() {
        return "localization";
    }

    @Override
    public @NotNull String getDescription() {
        return "\nHighlight important information in the messages using the primary color.";
    }

    @Override
    public @NotNull Boolean getDefault() {
        return true;
    }

    @Override
    public int getPriority() {
        return 10;
    }
}
