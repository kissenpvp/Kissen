package net.kissenpvp.core.ban.events.ban;

import net.kissenpvp.core.api.ban.AbstractBanTemplate;
import org.jetbrains.annotations.NotNull;

public class BanDeleteEvent<B extends AbstractBanTemplate> extends BanEvent<B>{

    /**
     * Constructs a {@link BanEvent} with the specified {@link AbstractBanTemplate} object.
     * This constructor initializes the ban event with the given ban and sets the event's cancel status to false.
     * <p>
     * This constructor should be used to create a new ban event. The created event can then be processed by the system or cancel.
     *
     * @param ban the {@link AbstractBanTemplate} this event relates to. This parameter must not be null, as indicated by the {@link NotNull} annotation.
     * @see BanEvent
     * @see AbstractBanTemplate
     * @see NotNull
     */
    public BanDeleteEvent(@NotNull B ban) {
        super(ban);
    }
}
