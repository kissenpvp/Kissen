package net.kissenpvp.core.ban.events.ban;

import lombok.Getter;
import lombok.Setter;
import net.kissenpvp.core.api.ban.Ban;
import net.kissenpvp.core.api.event.Cancellable;
import net.kissenpvp.core.api.event.EventClass;
import org.jetbrains.annotations.NotNull;

/**
 * The {@link BanEvent} class defines the events that are triggered when a {@link Ban} is enacted.
 * It extends {@link EventClass} which specifies that this class defines an event occurrence in a system,
 * and implements {@link Cancellable} which specifies that this event can be cancelled.
 * <p>
 * The {@link BanEvent} class is parameterized with a subclass of {@link Ban}, which represents a specific type of ban.
 * This class provides methods for getting and setting the ban that the event is related to, as well as a method for cancelling the event.
 * <p>
 * This class encapsulates a ban event and provides a way to interact with the event controller,
 * allowing other components of the system to cancel the event or manipulate the corresponding ban object.
 * <p>
 * This class should be used in conjunction with the {@link Ban} class, which represents a single ban,
 * and the {@link EventClass} and {@link Cancellable} interfaces, which provide the necessary functionalities for defining and manipulating events.
 * <p>
 * This class has {@link Getter} and {@link Setter} annotations, indicating that getter and setter methods are autogenerated by the Lombok library.
 *
 * @see EventClass
 * @see Cancellable
 * @see Ban
 * @see Getter
 * @see Setter
 */
@Getter
@Setter
public abstract class BanEvent<B extends Ban> implements EventClass, Cancellable {

    private boolean cancelled;
    private final B ban;

    /**
     * Constructs a {@link BanEvent} with the specified {@link Ban} object.
     * This constructor initializes the ban event with the given ban and sets the event's cancelled status to false.
     * <p>
     * This constructor should be used to create a new ban event. The created event can then be processed by the system or cancelled.
     *
     * @param ban the {@link Ban} this event relates to. This parameter must not be null, as indicated by the {@link NotNull} annotation.
     *
     * @see BanEvent
     * @see Ban
     * @see NotNull
     */
    public BanEvent(@NotNull B ban) {
        this.ban = ban;
        this.cancelled = false;
    }
}