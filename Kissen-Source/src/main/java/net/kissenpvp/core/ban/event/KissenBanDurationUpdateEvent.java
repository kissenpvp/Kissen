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

package net.kissenpvp.core.ban.event;

import lombok.Getter;
import lombok.Setter;
import net.kissenpvp.core.api.ban.Ban;
import net.kissenpvp.core.api.ban.event.BanDurationUpdateEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Optional;

@Getter @Setter
public class KissenBanDurationUpdateEvent implements BanDurationUpdateEvent
{
    private final Ban ban;
    private Duration duration;
    private boolean cancelled;

    public KissenBanDurationUpdateEvent(@NotNull Ban ban, @Nullable Duration duration)
    {
        this.ban = ban;
        this.duration = duration;
        this.cancelled = false;
    }

    @Override public @NotNull Optional<@Nullable Duration> getDuration()
    {
        return Optional.ofNullable(duration);
    }

}
