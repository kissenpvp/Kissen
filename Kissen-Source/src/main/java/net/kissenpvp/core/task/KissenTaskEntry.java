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

package net.kissenpvp.core.task;

import lombok.Getter;
import lombok.Setter;
import net.kissenpvp.core.api.task.TaskEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class KissenTaskEntry implements TaskEntry {
    @Getter
    private final String id, group;
    @Getter
    private final Runnable runnable;
    @Getter
    private final int tickRate;
    @Getter
    private final boolean async;
    @Getter
    @Setter
    private int listener;
    @Getter
    @Setter
    private boolean running;

    public KissenTaskEntry(@NotNull String id, @Nullable String group, int tickRate, boolean running, boolean async, @NotNull Runnable runnable) {
        this.id = id;
        this.group = group;
        this.tickRate = tickRate;
        this.runnable = runnable;
        this.running = running;
        this.async = async;
        this.listener = -1;
    }

    @Override
    public void end() {
        setRunning(false);
    }

    @Override
    public String toString() {
        return "KissenTask{" + "ID='" + id + '\'' + ", GROUP='" + group + '\'' + ", RUNNABLE=" + runnable + ", ASYNC=" + async + ", listener=" + listener + ", running=" + running + '}';
    }
}
