package net.kissenpvp.core.ban.events.punishment;

import lombok.Getter;
import net.kissenpvp.core.api.ban.Punishment;
import net.kissenpvp.core.api.message.Comment;
import org.jetbrains.annotations.NotNull;

/**
 * The {@link PunishmentCommentEvent} class extends the {@link PunishmentEvent} class, providing functionality for managing comment events related to a specific punishment.
 * This class introduces a new property, {@code comment}, representing the comment related to the punishment.
 * <p>
 * A PunishmentCommentEvent is triggered whenever a new comment is related to a specific punishment of type P.
 * This class encapsulates that action by keeping a reference to the associated comment.
 * <p>
 * This class retains all the functionality of the {@link PunishmentEvent} class and adds to it by providing access to the associated {@link Comment}.
 * <p>
 * This class should be used whenever a comment event related to a specific punishment needs to be created, manipulated, or accessed.
 * This class has a {@link Getter} annotation, indicating that getter methods are autogenerated by the Lombok library for its fields.
 *
 * @param <P> The type of punishment managed by this event, this type extends {@link Punishment}
 *
 * @see PunishmentEvent
 * @see Comment
 * @see Getter
 */
@Getter
public class PunishmentCommentEvent<P extends Punishment<?>> extends PunishmentEvent<P> {

    private final Comment comment;

    /**
     * The {@link PunishmentCommentEvent} class extends the {@link PunishmentEvent} class, providing functionality for managing comment events related to a specific punishment.
     * This class introduces a new property, {@code comment}, representing the comment related to the punishment.
     * <p>
     * A PunishmentCommentEvent is triggered whenever a new comment is related to a specific punishment of type P.
     * This class encapsulates that action by keeping a reference to the associated comment.
     * <p>
     * This class retains all the functionality of the {@link PunishmentEvent} class and adds to it by providing access to the associated {@link Comment}.
     * <p>
     * This class should be used whenever a comment event related to a specific punishment needs to be created, manipulated, or accessed.
     * This class has a {@link Getter} annotation, indicating that getter methods are autogenerated by the Lombok library for its fields.
     *
     * @see PunishmentEvent
     * @see Comment
     * @see Getter
     */
    public PunishmentCommentEvent(@NotNull P punishment, @NotNull Comment comment) {
        super(punishment);
        this.comment = comment;
    }
}