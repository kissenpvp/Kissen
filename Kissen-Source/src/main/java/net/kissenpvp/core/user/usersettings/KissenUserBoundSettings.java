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

import lombok.Getter;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.permission.PermissionEntry;
import net.kissenpvp.core.api.user.User;
import net.kissenpvp.core.api.user.exception.UnauthorizedException;
import net.kissenpvp.core.api.user.usersetttings.PlayerSetting;
import net.kissenpvp.core.api.user.usersetttings.UserSetting;
import net.kissenpvp.core.api.user.usersetttings.UserValue;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Getter
public class KissenUserBoundSettings<T> extends KissenUserSettings<T> implements UserSetting<T> {
    private final User user;

    public KissenUserBoundSettings(@NotNull PlayerSetting<T> playerSetting, @NotNull User user) {
        super(playerSetting);
        this.user = user;
    }

    @Override
    public @NotNull T setValue(@NotNull T value) throws UnauthorizedException {
        T oldValue = getValue();
        PlayerClient<?, ?, ?> playerClient = user.getPlayerClient();

        if (Objects.equals(getUserSetting().getDefaultValue(playerClient), value)) {
            reset();
            return oldValue;
        }

        UserValue<T>[] possibilities = getUserSetting().getPossibleValues(playerClient); //Get all possibilities

        if(possibilities.length != 0)
        {
            Optional<UserValue<T>> currentPossibility = Arrays.stream(possibilities).filter(possibility -> possibility.value().equals(value)).findFirst(); //find the one the user wants to set

            if (currentPossibility.isEmpty()) // throw exception if value is not listed as option
            {
                throw new IllegalArgumentException("Value is not listed as possible value.");
            }

            UserValue<T> currentValue = currentPossibility.get();
            if (currentValue.permission().length > 0 && !value.equals(getUserSetting().getDefaultValue(playerClient))) {
                Optional<String> permission = Arrays.stream(currentValue.permission()).filter(
                        currentPermission -> !((PermissionEntry<?>) getUser().getPlayerClient()).hasPermission(
                                currentPermission)).toList().stream().findFirst();
                if (permission.isPresent()) {
                    throw new UnauthorizedException(UUID.fromString(getUser().getRawID()), permission.get());
                }
            }
        }

        getUserSetting().setValue(value);
        getUser().set("setting_" + getUserSetting().getKey(), getUserSetting().serialize(value));
        return oldValue;
    }

    @Override
    public @NotNull T getValue() {
        T defaultValue = getUserSetting().getDefaultValue(user.getPlayerClient());
        Optional<String> value = getUser().get("setting_" + getUserSetting().getKey());
        return value.map(val -> getUserSetting().deserialize(val)).orElse(defaultValue);
    }

    @Override public void reset() {
        getUser().delete("setting_" + getUserSetting().getKey());
    }
}
