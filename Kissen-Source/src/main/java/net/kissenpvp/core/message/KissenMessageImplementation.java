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