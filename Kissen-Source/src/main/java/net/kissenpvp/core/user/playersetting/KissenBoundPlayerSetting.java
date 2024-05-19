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
import net.kissenpvp.core.api.database.savable.SavableMap;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.permission.AbstractPermissionEntry;
import net.kissenpvp.core.api.user.User;
import net.kissenpvp.core.api.user.exception.UnauthorizedException;
import net.kissenpvp.core.api.user.playersettting.AbstractBoundPlayerSetting;
import net.kissenpvp.core.api.user.playersettting.RegisteredPlayerSetting;
import net.kissenpvp.core.api.user.playersettting.UserValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Getter
public abstract class KissenBoundPlayerSetting<T, S extends PlayerClient<?, ?>> extends KissenUserSetting<T, S> implements AbstractBoundPlayerSetting<T> {
    private final S player;

    public KissenBoundPlayerSetting(@NotNull RegisteredPlayerSetting<T, S> setting, @NotNull S player) {
        super(setting);
        this.player = player;
    }

    protected void isAllowed(@NotNull T value, @NotNull UserValue<T>[] possibilities) throws UnauthorizedException {
        Optional<UserValue<T>> currentPossibility = Arrays.stream(possibilities).filter(possibility -> Objects.equals(possibility.value(), value)).findFirst(); // find the one the user wants to set

        if (currentPossibility.isEmpty()) // throw exception if value is not listed as option
        {
            throw new IllegalArgumentException("Value is not listed as possible value.");
        }

        UserValue<T> currentValue = currentPossibility.get();
        if (currentValue.permission().length > 0 && !value.equals(getSetting().getParent().getDefaultValue(getPlayer()))) {

            Predicate<String> isNotAllowed = currentPermission -> {
                AbstractPermissionEntry<?> permissionEntry = (AbstractPermissionEntry<?>) getPlayer();
                return !permissionEntry.hasPermission(currentPermission);
            };

            Stream<String> valueStream = Arrays.stream(currentValue.permission());
            Optional<String> permission = valueStream.filter(isNotAllowed).toList().stream().findFirst();

            if (permission.isPresent()) {
                throw new UnauthorizedException(getPlayer().getUniqueId(), permission.get());
            }
        }

        getRepository(getPlayer().getUser()).set(getKey(), getSetting().getParent().serialize(value));
    }

    protected @NotNull Optional<T> getValue(@NotNull User user) {
        SavableMap repo = user.getRepository(getSetting().getPlugin());
        return repo.get(getKey(), String.class).map(currentValue -> getSetting().getParent().deserialize(currentValue));
    }

    protected boolean reset(@NotNull User user) {
        return Objects.nonNull(getRepository(user).delete(getKey()));
    }

    @Contract(pure = true, value = "-> new")
    protected @NotNull String getKey() {
        return String.format("%s_setting_%s", getSetting().getPlugin().getName(), getSetting().getParent().getKey());
    }
}
