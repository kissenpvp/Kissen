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

package net.kissenpvp.core.user.usersettings;

import net.kissenpvp.core.api.base.loader.Loadable;
import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import net.kissenpvp.core.api.reflection.ReflectionClass;
import net.kissenpvp.core.api.user.UserImplementation;
import net.kissenpvp.core.api.user.usersetttings.PlayerSetting;
import net.kissenpvp.core.base.KissenCore;
import org.jetbrains.annotations.NotNull;

public class UserSettingLoader implements Loadable {
    @Override
    public boolean isLoadable(@NotNull ReflectionClass clazz, @NotNull KissenPlugin plugin) {
        return !clazz.isAbstract() && !clazz.isInterface() && PlayerSetting.class.isAssignableFrom(clazz.getJavaClass());
    }

    @Override
    public void load(@NotNull ReflectionClass clazz, @NotNull KissenPlugin plugin) {
        KissenCore.getInstance().getLogger().debug("Register user setting '{}'.", clazz.getJavaClass().getSimpleName());
        KissenCore.getInstance().getImplementation(UserImplementation.class).registerUserSetting((PlayerSetting<?>) clazz.getInstance());
    }

    @Override
    public void enable(@NotNull ReflectionClass clazz, @NotNull KissenPlugin plugin) {
    }
}
