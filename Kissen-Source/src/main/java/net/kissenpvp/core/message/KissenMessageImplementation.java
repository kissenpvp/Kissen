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

package net.kissenpvp.core.message;

import net.kissenpvp.core.api.config.ConfigurationImplementation;
import net.kissenpvp.core.api.message.MessageImplementation;
import net.kissenpvp.core.api.message.Theme;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.message.settings.DefaultColor;
import net.kissenpvp.core.message.settings.DefaultPrimaryColor;
import net.kissenpvp.core.message.settings.DefaultSecondaryColor;


public class KissenMessageImplementation implements MessageImplementation {
    @Override
    public String getKey(String key, String value) {
        return KissenCore.getInstance().getImplementation(ConfigurationImplementation.class).getSetting(DefaultSecondaryColor.class) + key + " " + KissenCore.getInstance().getImplementation(ConfigurationImplementation.class).getSetting(DefaultColor.class) + "» " + KissenCore.getInstance().getImplementation(ConfigurationImplementation.class).getSetting(DefaultPrimaryColor.class) + value;
    }

    @Override
    public int count(String input, String searchFor) {
        return 0;
    }

    @Override
    public Theme getDefaultTheme() {
        return new DefaultTheme();
    }
}