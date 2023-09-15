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

package net.kissenpvp.core.api.task;

import net.kissenpvp.core.api.base.Implementation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;


public interface TaskImplementation extends Implementation
{
    @NotNull TaskEntry registerTask(@NotNull String id, int tickRate, Runnable runnable) throws TaskException;

    @NotNull TaskEntry registerTask(@NotNull String id, String group, int tickRate, Runnable runnable) throws TaskException;

    @NotNull TaskEntry registerTask(@NotNull String id, String group, int tickRate, boolean isRunning,
                                    Runnable runnable) throws TaskException;

    @NotNull TaskEntry registerAsyncTask(@NotNull String id, int tickRate, Runnable runnable) throws TaskException;

    @NotNull TaskEntry registerAsyncTask(@NotNull String id, String group, int tickRate, Runnable runnable) throws TaskException;

    @NotNull TaskEntry registerAsyncTask(@NotNull String id, String group, int tickRate, boolean isRunning,
                                         Runnable runnable) throws TaskException;

    @NotNull TaskEntry registerTask(@NotNull String id, String group, int tickRate, boolean isRunning, boolean async,
                                    Runnable runnable) throws TaskException;

    boolean endTask(@NotNull String id);

    int endTasks(@NotNull String group);

    int endTasks();

    void killTask(@NotNull String id);

    void killTasks(@NotNull String group);

    void killTasks();

    TaskEntry getTask(@NotNull String id);

    @Unmodifiable @NotNull Set<TaskEntry> getTasks();

    @Unmodifiable @NotNull Set<TaskEntry> getTasks(@NotNull String group);
}
