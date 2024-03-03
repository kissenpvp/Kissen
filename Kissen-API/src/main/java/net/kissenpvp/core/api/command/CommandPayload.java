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

package net.kissenpvp.core.api.command;

import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

public interface CommandPayload<S extends ServerEntity>
{

    @NotNull String getLabel();

    @NotNull S getSender();

    @NotNull CommandTarget getTarget();

    @NotNull String[] getArguments();

    default int getArgumentCount()
    {
        return getArguments().length;
    }

    default @NotNull Optional<String> getArgument(int index)
    {
        try
        {
            return Optional.of(getArguments()[index]);
        }
        catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException)
        {
            return Optional.empty();
        }
    }

    default String[] getArguments(int from, int to) throws ArrayIndexOutOfBoundsException
    {
        return Arrays.copyOfRange(getArguments(), from, to);
    }

    <T> @NotNull T[] getArgument(int from, int to, @NotNull Class<T> type) throws ArrayIndexOutOfBoundsException;

    boolean validate(@NotNull ServerEntity serverEntity);

    @NotNull Builder confirmRequest(@NotNull Runnable runnable);

    interface Builder
    {
        @NotNull Builder setDuration(Duration duration);

        @NotNull Builder setRunnable(Runnable runnable);

        @NotNull Builder onCancel(Runnable cancel);

        @NotNull Builder onTime(Runnable timeRunOut);

        @NotNull Builder suppressMessage(boolean suppressMessage);

        void send();
    }
}
