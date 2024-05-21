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

@Getter
public abstract class KissenBoundPlayerSetting<T, S extends PlayerClient<?, ?>> extends KissenUserSetting<T, S> implements AbstractBoundPlayerSetting<T> {
    private final S player;

    public KissenBoundPlayerSetting(@NotNull RegisteredPlayerSetting<T, S> setting, @NotNull S player) {
        super(setting);
        this.player = player;
    }

    private static <T> @NotNull Optional<UserValue<T>> findMatchingPossibility(@NotNull T value, @NotNull UserValue<T>[] possibilities) {
        Predicate<UserValue<T>> equals = possibility -> Objects.equals(possibility.value(), value);
        return Arrays.stream(possibilities).filter(equals).findFirst();
    }

    private static <T> UserValue<T> loadMatchingPossibility(@NotNull T value, @NotNull UserValue<T>[] possibilities) {
        return findMatchingPossibility(value, possibilities).orElseThrow(() -> {
            String message = String.format("The value %s is not registered as possible possibility.", value);
            return new IllegalArgumentException(message);
        });
    }

    protected void isAllowed(@NotNull T value, @NotNull UserValue<T>[] possibilities) throws UnauthorizedException {
        UserValue<T> possibility = loadMatchingPossibility(value, possibilities);

        // Users always can select the default value
        if (Objects.equals(value, getSetting().getParent().getDefaultValue(getPlayer()))) {
            return;
        }

        AbstractPermissionEntry<?> permissionEntry = (AbstractPermissionEntry<?>) getPlayer();

        // If possibility#permission() is empty, this will be skipped.
        for (String permission : possibility.permission()) {
            if (!permissionEntry.hasPermission(permission)) {
                throw new UnauthorizedException(getPlayer().getUniqueId(), permission);
            }
        }
    }

    protected @NotNull Optional<T> getValue(@NotNull User user) {
        return getRepository(user).get(getKey(), String.class).map(value -> getSetting().getParent().deserialize(value));
    }

    protected void setValue(@NotNull User user, @NotNull T value) {
        getSetting().getParent().setValue(getPlayer(), value);
        getRepository(user).set(getKey(), getSetting().getParent().serialize(value));
    }

    protected boolean reset(@NotNull User user) {
        return Objects.nonNull(getRepository(user).delete(getKey()));
    }

    @Contract(pure = true, value = "-> new")
    protected @NotNull String getKey() {
        return String.format("%s_setting_%s", getSetting().getPlugin().getName(), getSetting().getParent().getKey());
    }
}
