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

package net.kissenpvp.core.api.user.playersettting;

import net.kissenpvp.core.api.base.serializer.TextSerializer;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AbstractPlayerSetting<T, S extends PlayerClient<?, ?>> extends TextSerializer<T> {

    @NotNull
    String getKey();

    @NotNull
    T getDefaultValue(@NotNull S player);

    default @NotNull UserValue<T>[] getPossibleValues(@NotNull S player) {
        return new UserValue[0];
    }

    default @Nullable String getPermission() {
        return null;
    }

    default boolean hasPermission() {
        return true;
    }

    default void setValue(@NotNull S player, @Nullable T value) {
    }

    default void reset(@NotNull S player) {
    }
}
