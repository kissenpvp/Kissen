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
package net.kissenpvp.core.event;

import net.kissenpvp.core.api.event.Cancellable;
import net.kissenpvp.core.api.event.EventClass;
import net.kissenpvp.core.api.event.EventImplementation;
import net.kissenpvp.core.api.event.EventListener;
import net.kissenpvp.core.base.KissenCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class KissenEventImplementation implements EventImplementation {
    private final Set<EventListener<?>> eventListenerSet;

    public KissenEventImplementation() {
        this.eventListenerSet = new HashSet<>();
    }

    @Override
    public boolean preStart() {
        //TODO search for event
        return EventImplementation.super.preStart();
    }

    @Override
    public @Unmodifiable @NotNull Set<?> getEventListener() {
        return Collections.unmodifiableSet(eventListenerSet);
    }

    @Override
    public void registerEvent(EventListener<?> eventListener) {
        eventListenerSet.add(eventListener);
        KissenCore.getInstance().getLogger().debug("Register internal async listener '{}'.", eventListener.getClass().getSimpleName());
    }

    @Override
    public <T extends EventClass> boolean call(@NotNull T event) {
        getEventListener().forEach(currentEvent ->
        {
            try {
                if (event instanceof Cancellable cancellable && cancellable.isCancelled()) {
                    return;
                }
                ((EventListener<T>) currentEvent).call(event);
            } catch (ClassCastException ignored) {
            }
        });

        return !(event instanceof Cancellable) || !((Cancellable) event).isCancelled();
    }
}
