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

import lombok.Getter;
import net.kissenpvp.core.api.user.User;
import net.kissenpvp.core.api.user.UserImplementation;
import net.kissenpvp.core.api.user.usersetttings.PlayerSetting;
import net.kissenpvp.core.api.user.usersetttings.UserSetting;
import net.kissenpvp.core.api.user.usersetttings.UserValue;
import net.kissenpvp.core.base.KissenCore;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class KissenUserBoundSettings<T> extends KissenUserSettings<T> implements UserSetting<T> {
    @Getter
    private final User user;

    public KissenUserBoundSettings(@NotNull PlayerSetting<T> playerSetting, @NotNull UUID uuid) {
        super(playerSetting);
        this.user = KissenCore.getInstance().getImplementation(UserImplementation.class).getUser(uuid);
    }

    @Override
    public @NotNull T setValue(@NotNull T value) {
        T oldValue = getValue();

        /*if (getUserSetting().getPermission().map(currentPermission -> !getUser().hasPermission(currentPermission)).orElse(false)) {
            throw new UnauthorizedException(getUserSetting().getPermission().get()); //TODO
        }*/

        if (Objects.equals(getUserSetting().getDefaultValue(), value)) {
            user.delete("setting_" + getUserSetting().getKey());
            return oldValue;
        }

        UserValue<T>[] possibilities = getUserSetting().getPossibleValues(); //Get all possibilities
        Optional<UserValue<T>> currentPossibility = Arrays.stream(possibilities).filter(possibility -> possibility.value().equals(value)).findFirst(); //find the one the user wants to set

        if (currentPossibility.isEmpty()) // throw exception if value is not listed as option
        {
            throw new IllegalArgumentException("Value is not listed as possible value.");
        }

        UserValue<T> currentValue = currentPossibility.get();
        if (currentValue.permission().length > 0) {
            /*Optional<String> permission = Arrays.stream(currentValue.permission()).filter(currentPermission -> !getUser().getPlayerClient().hasPermission(currentPermission)).toList().stream().findFirst();
            if (permission.isPresent()) {
                throw new UnauthorizedException(permission.get());
            }*/
            //TODO
        }

        getUserSetting().setValue(value);
        user.set("setting_" + getUserSetting().getKey(), getUserSetting().serialize(value));
        return oldValue;
    }

    @Override
    public @NotNull T getValue() {
        T defaultValue = getUserSetting().getDefaultValue();
        Optional<String> value = user.get("setting_" + getUserSetting().getKey());
        return value.map(val -> getUserSetting().deserialize(val)).orElse(defaultValue);
    }

    @Override
    public void reset() {
        setValue(getUserSetting().getDefaultValue());
    }
}
