package net.kissenpvp.core.ban.events.ban;

import net.kissenpvp.core.api.ban.AbstractBan;
import org.jetbrains.annotations.NotNull;

public class BanDeleteEvent<B extends AbstractBan> extends BanEvent<B>{

    /**
     * Constructs a {@link BanEvent} with the specified {@link AbstractBan} object.
     * This constructor initializes the ban event with the given ban and sets the event's cancel status to false.
     * <p>
     * This constructor should be used to create a new ban event. The created event can then be processed by the system or cancel.
     *
     * @param ban the {@link AbstractBan} this event relates to. This parameter must not be null, as indicated by the {@link NotNull} annotation.
     * @see BanEvent
     * @see AbstractBan
     * @see NotNull
     */
    public BanDeleteEvent(@NotNull B ban) {
        super(ban);
    }
}
