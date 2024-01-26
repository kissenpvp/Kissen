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

package net.kissenpvp.core.command;

import net.kissenpvp.core.api.command.CommandTarget;
import net.kissenpvp.core.api.networking.client.entitiy.ConsoleClient;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import org.jetbrains.annotations.NotNull;

public class TargetValidator
{
    public boolean validate(@NotNull CommandTarget commandTarget, @NotNull ServerEntity serverEntity) {
        return switch (commandTarget) {
            case PLAYER -> serverEntity instanceof PlayerClient<?, ?, ?>;
            case SYSTEM -> serverEntity instanceof ConsoleClient;
            case ALL -> true;
        };
    }

    public @NotNull CommandTarget parseSender(@NotNull ServerEntity serverEntity) {
        if (serverEntity instanceof ConsoleClient) {
            return CommandTarget.SYSTEM;
        }

        if (serverEntity instanceof PlayerClient<?, ?, ?>) {
            return CommandTarget.PLAYER;
        }

        throw new IllegalArgumentException(String.format("No suitable target type has been found for target %s.", serverEntity.getClass()
            .getName()));
    }
}
