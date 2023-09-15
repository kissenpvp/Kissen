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

package net.kissenpvp.core.task;

import lombok.Getter;
import lombok.Synchronized;
import net.kissenpvp.core.api.task.TaskBindException;
import net.kissenpvp.core.api.task.TaskEntry;
import net.kissenpvp.core.api.task.TaskImplementation;
import net.kissenpvp.core.base.KissenCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


public class KissenTaskImplementation implements TaskImplementation {

    private final @NotNull Set<TaskEntry> taskEntries;
    @Getter(onMethod_ = {@Synchronized})
    private final @NotNull Set<TaskEntry> queuedTasks;

    /**
     * Sets the {@link #taskEntries} options to its default value.
     * That's it.
     */
    public KissenTaskImplementation() {
        this.taskEntries = new HashSet<>();
        this.queuedTasks = new HashSet<>();
    }

    @Override
    public boolean start() {
        KissenCore.getInstance().getLogger().debug("Start task thread.");

        Thread thread = new Thread(() ->
        {
            while (true) {
                try {
                    getQueuedTasks().forEach(task ->
                    {
                        long time = System.currentTimeMillis();

                        if (task.isAsync()) {
                            task.getRunnable().run();
                        } else {
                            KissenCore.getInstance().runTask(0, task.getRunnable());
                        }

                        time = System.currentTimeMillis() - time;

                        if (time > 1000) {
                            KissenCore.getInstance().getLogger().warn("Task '{}' took {}ms to execute!", task.getId(), time);
                        }
                        getQueuedTasks().remove(task);
                    });
                } catch (ConcurrentModificationException ignored) {
                }
            }
        });
        thread.setName("Task thread");
        thread.start();

        KissenCore.getInstance().repeatTask("tasks", 0, new Runnable() {
            private int tick = 3;

            @Override
            public void run() {
                if (!taskEntries.isEmpty()) {
                    taskEntries.forEach(task ->
                    {
                        if (task.isRunning()) {
                            if (task.getListener() < 0) {
                                task.setListener(task.getTickRate() % 2 == 0 ? tick % 2 == 0 ? tick - 1 : tick : tick % 2 == 0 ? tick : tick - 1);
                            }

                            if (tick % task.getTickRate() == task.getListener()) {
                                getQueuedTasks().add(task);
                            }
                        }
                    });
                }

                if (tick > Integer.MAX_VALUE - 2) {
                    tick = 3;
                    taskEntries.forEach(task -> task.setListener(tick));
                }
                tick++;
            }
        });

        return true;
    }

    @Override
    public @NotNull TaskEntry registerTask(@NotNull String id, int tickRate, Runnable runnable) throws TaskBindException {
        return registerTask(id, null, tickRate, runnable);
    }

    @Override
    public @NotNull TaskEntry registerTask(@NotNull String id, String group, int tickRate,
                                           Runnable runnable) throws TaskBindException {
        return registerTask(id, group, tickRate, true, runnable);
    }

    @Override
    public @NotNull TaskEntry registerTask(@NotNull String id, String group, int tickRate,
                                           boolean isRunning, Runnable runnable) throws TaskBindException {
        return registerTask(id, group, tickRate, isRunning, false, runnable);
    }

    @Override
    public @NotNull TaskEntry registerAsyncTask(@NotNull String id, int tickRate, Runnable runnable) throws TaskBindException {
        return registerAsyncTask(id, null, tickRate, runnable);
    }

    @Override
    public @NotNull TaskEntry registerAsyncTask(@NotNull String id, String group, int tickRate,
                                                Runnable runnable) throws TaskBindException {
        return registerAsyncTask(id, group, tickRate, true, runnable);
    }

    @Override
    public @NotNull TaskEntry registerAsyncTask(@NotNull String id, String group, int tickRate,
                                                boolean isRunning, Runnable runnable) throws TaskBindException {
        return registerTask(id, group, tickRate, isRunning, true, runnable);
    }

    @Override
    public @NotNull TaskEntry registerTask(@NotNull String id, String group, int tickRate,
                                           boolean isRunning, boolean async, Runnable runnable) throws TaskBindException {
        if (getTask(id) != null) {
            throw new TaskBindException(id);
        }

        TaskEntry task = new KissenTaskEntry(id, group, tickRate, true, async, runnable);
        taskEntries.add(task);
        KissenCore.getInstance().getLogger().debug("Registered task '{}'.", task.getId());
        return task;
    }

    @Override
    public boolean endTask(@NotNull String id) {
        final Set<TaskEntry> taskSet =
                taskEntries.stream().filter(task -> task.getId().equals(id)).collect(Collectors.toSet());
        if (taskSet.isEmpty()) {
            KissenCore.getInstance().getLogger().debug("Tried to end task '{}', but no task was found.", id);
            return false;
        }
        taskSet.forEach(TaskEntry::end);
        taskEntries.removeAll(taskSet);
        return true;
    }

    @Override
    public int endTasks(@NotNull String group) {
        return (int) taskEntries.stream().filter(task -> task.getGroup().equals(group)).map(TaskEntry::getId).toList().stream().filter(this::endTask).count();
    }

    @Override
    public int endTasks() {
        return (int) taskEntries.stream().map(TaskEntry::getId).toList().stream().filter(this::endTask).count();
    }

    @Override
    public void killTask(@NotNull String id) {
        taskEntries.stream().filter(task -> task.getId().equals(id)).toList().forEach(taskEntries::remove);
    }

    @Override
    public void killTasks(@NotNull String group) {
        taskEntries.stream().filter(task -> task.getGroup().equals(group)).toList().forEach(taskEntries::remove);
    }

    @Override
    public void killTasks() {
        taskEntries.clear();
    }

    @Override
    public TaskEntry getTask(@NotNull String id) {
        return taskEntries.stream().filter(task -> task.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public @Unmodifiable @NotNull Set<TaskEntry> getTasks() {
        return Collections.unmodifiableSet(taskEntries);
    }

    @Override
    public @Unmodifiable @NotNull Set<TaskEntry> getTasks(@NotNull String group) {
        return taskEntries.stream().filter(task -> task.getGroup().equals(group)).collect(Collectors.toUnmodifiableSet());
    }
}
