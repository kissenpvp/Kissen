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
