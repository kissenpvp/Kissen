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
package net.kissenpvp.core.event;

import net.kissenpvp.core.api.event.Cancellable;
import net.kissenpvp.core.api.event.EventClass;
import net.kissenpvp.core.api.event.EventImplementation;
import net.kissenpvp.core.api.event.EventListener;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.command.KissenCommandImplementation;
import net.kissenpvp.core.message.localization.KissenLocalizationImplementation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class KissenEventImplementation implements EventImplementation {
    private final Set<EventListener<?>> eventListenerSet;

    public KissenEventImplementation() {
        this.eventListenerSet = new HashSet<>();
    }

    @Override
    public boolean start()
    {
        KissenLocalizationImplementation loc = KissenCore.getInstance().getImplementation(KissenLocalizationImplementation.class);
        loc.register("server.event.cancel", new MessageFormat("The action has not been executed."));

        KissenCommandImplementation command = KissenCore.getInstance().getImplementation(KissenCommandImplementation.class);
        command.registerHandler(new EventCancelledHandler<>());
        return EventImplementation.super.start();
    }

    @Override
    public @Unmodifiable @NotNull Set<?> getEventListener() {
        return Collections.unmodifiableSet(eventListenerSet);
    }

    @Override
    public void registerEvent(@NotNull EventListener<?>... eventListener) {
        eventListenerSet.addAll(List.of(eventListener));
        KissenCore.getInstance().getLogger().debug("Register internal listener '{}'.", eventListener.getClass().getSimpleName());
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
