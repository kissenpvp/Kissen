package net.kissenpvp.core.ban.events.ban;

import net.kissenpvp.core.api.ban.Ban;
import org.jetbrains.annotations.NotNull;

public class BanCreateEvent<B extends Ban> extends BanEvent<B> {

    /**
     * Constructs a {@link BanEvent} with the specified {@link Ban} object.
     * This constructor initializes the ban event with the given ban and sets the event's cancel status to false.
     * <p>
     * This constructor should be used to create a new ban event. The created event can then be processed by the system or cancel.
     *
     * @param ban the {@link Ban} this event relates to. This parameter must not be null, as indicated by the {@link NotNull} annotation.
     * @see BanEvent
     * @see Ban
     * @see NotNull
     */
    public BanCreateEvent(@NotNull B ban) {
        super(ban);
    }
}
