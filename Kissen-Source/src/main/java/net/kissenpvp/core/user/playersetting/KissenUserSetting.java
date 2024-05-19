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

package net.kissenpvp.core.user.playersetting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kissenpvp.core.api.database.savable.SavableMap;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.user.User;
import net.kissenpvp.core.api.user.playersettting.AbstractBoundPlayerSetting;
import net.kissenpvp.core.api.user.playersettting.RegisteredPlayerSetting;
import org.jetbrains.annotations.NotNull;

@Getter @RequiredArgsConstructor
public abstract class KissenUserSetting<T, S extends PlayerClient<?, ?>> implements AbstractBoundPlayerSetting<T>
{
    private final RegisteredPlayerSetting<T, S> setting;

    protected @NotNull SavableMap getRepository(@NotNull User user)
    {
        return user.getRepository(getSetting().getPlugin());
    }

}
