package net.kissenpvp.core.ban.events.ban;

import lombok.Getter;
import lombok.Setter;
import net.kissenpvp.core.api.ban.AbstractBan;
import org.jetbrains.annotations.NotNull;

/**
 * The {@link BanRenameEvent} class extends the {@link BanEvent} class to provide functionality for managing ban type alteration events.
 * This class introduces new properties to represent the old name and the new name of the ban type.
 * <p>
 * A ban alteration event is triggered when the name of a ban type is changed. This class encapsulates that action by keeping a reference to the ban type's old name and the new name.
 * <p>
 * This class retains all the functionality of the {@link BanEvent} class and adds to it by providing access to the ban type's old and new names.
 * This class has {@link Getter} and {@link Setter} annotations, indicating that getter and setter methods are autogenerated by the Lombok library.
 *
 * @param <B> The type of ban managed by this event, this type extends {@link AbstractBan}
 * @see BanEvent
 */
@Getter @Setter
public class BanRenameEvent<B extends AbstractBan> extends BanEvent<B> {

    private final String previousName;
    private String updateName;

    /**
     * Constructs a new {@link BanRenameEvent} with the given ban, old name of the ban's type, and new name of the ban type.
     * This constructor calls its superclass's constructor with the ban and initializes the oldName and newName properties.
     * <p>
     * This constructor is used to create a new ban name alteration event which is triggered when an existing ban's type name is changed.
     *
     * @param ban the {@link AbstractBan} related to this ban alteration event. This parameter must not be null.
     * @param previousName the old name of the ban type before the change. This parameter must not be null.
     * @param updateName the new name of the ban type after the change. This parameter must not be null.
     *
     * @see AbstractBan
     * @see NotNull
     */
    public BanRenameEvent(@NotNull B ban, @NotNull String previousName, @NotNull String updateName) {
        super(ban);
        this.previousName = previousName;
        this.updateName = updateName;
    }
}
