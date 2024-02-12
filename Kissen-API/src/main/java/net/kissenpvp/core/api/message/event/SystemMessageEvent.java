package net.kissenpvp.core.api.message.event;

import net.kissenpvp.core.api.event.Cancellable;
import net.kissenpvp.core.api.event.EventClass;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.jetbrains.annotations.NotNull;

public class SystemMessageEvent implements EventClass, Cancellable {

    private final ServerEntity sender, receiver;
    private Component components;

    private boolean cancelled;

    public SystemMessageEvent(@NotNull ServerEntity sender, @NotNull ServerEntity receiver, @NotNull Component... components) {
        this.sender = sender;
        this.receiver = receiver;
        this.components = Component.join(JoinConfiguration.noSeparators(), components);
        this.cancelled = false;
    }

    @Override
    public boolean volatileEvent() {
        return true;
    }

    public @NotNull Component getComponent() {
        return components;
    }

    public void setComponent(@NotNull Component components) {
        this.components = components;
    }

    public @NotNull ServerEntity getSender() {
        return sender;
    }

    public @NotNull ServerEntity getReceiver() {
        return receiver;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
