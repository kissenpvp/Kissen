package net.kissenpvp.core.ban.events.ban;

import lombok.Getter;
import lombok.Setter;
import net.kissenpvp.core.api.ban.AbstractBanTemplate;
import net.kissenpvp.core.api.ban.BanType;
import org.jetbrains.annotations.NotNull;

/**
 * The {@link BanAlterTypeEvent} class extends the {@link BanEvent} class to provide functionality for managing ban rename events.
 * This class introduces new properties to represent the old type and the new type of the ban.
 * <p>
 * A ban rename event is triggered when the type of ban is changed.
 * This class encapsulates that action by keeping a reference to the old type and the new type.
 * <p>
 * This class retains all the functionality of the {@link BanEvent} class and adds to it by providing access to the ban's old and new type.
 *
 * @see BanEvent
 * @see BanType
 *
 * @param <B> The type of ban managed by this event, this type extends {@link AbstractBanTemplate}
 */
@Getter @Setter
public class BanAlterTypeEvent<B extends AbstractBanTemplate>  extends BanEvent<B> {

    private final BanType previousType;
    private BanType updatedType;

    /**
     * Constructs a new {@link BanAlterTypeEvent} with the given ban, old type, and new type.
     * This constructor calls its superclasses constructor with the ban and initializes the oldType and newType properties.
     * <p>
     * This constructor is used to create a new ban rename event which is triggered when an existing ban's type is changed.
     *
     * @param ban the {@link AbstractBanTemplate} related to this ban rename event. This parameter must not be null.
     * @param previousType the old {@link BanType} of the ban before the change. This parameter must not be null.
     * @param updatedType the new {@link BanType} of the ban after the change. This parameter must not be null.
     *
     * @see AbstractBanTemplate
     * @see BanType
     * @see NotNull
     */
    public BanAlterTypeEvent(@NotNull B ban, @NotNull BanType previousType, @NotNull BanType updatedType) {
        super(ban);
        this.previousType = previousType;
        this.updatedType = updatedType;
    }
}
