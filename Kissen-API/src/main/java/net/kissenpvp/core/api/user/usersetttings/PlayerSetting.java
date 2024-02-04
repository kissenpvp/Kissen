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

package net.kissenpvp.core.api.user.usersetttings;

import net.kissenpvp.core.api.base.serializer.TextSerializer;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PlayerSetting<T> extends TextSerializer<T> {

    @NotNull
    String getKey();

    @NotNull
    T getDefaultValue(@NotNull PlayerClient<?, ?, ?> playerClient);

    default @NotNull UserValue<T>[] getPossibleValues(@NotNull PlayerClient<?, ?, ?> playerClient) {
        return new UserValue[0];
    }

    default @NotNull String getPermission() {
        return "";
    }

    default boolean autoGeneratePermission() {
        return true;
    }

    default void setValue(@Nullable T value) {
    }
}
