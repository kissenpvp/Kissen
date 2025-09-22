package net.kissenpvp.api.database;

import org.jspecify.annotations.NonNull;

import java.util.Optional;

/**
 * Represents a generic entity that subscribes to or is associated with a parent {@link PersistableEntity}.
 * <p>
 * This interface introduces a relationship between child and parent
 * entities, where the parent is another instance of {@link PersistableEntity}.
 *
 * @param <P> the type of the primary identifier of this entity
 * @param <X> the type of the primary identifier of the parent entity
 * @param <T> the type of the parent entity, which must extend {@link PersistableEntity}
 * @author Ivo Quiring
 */
public interface SubscriptionEntity<P, X, T extends PersistableEntity<X>> extends PersistableEntity<P>
{
    /**
     * Retrieves the optional parent entity associated with this subscription entity.
     * <p>
     * The parent entity represents the overarching or linked {@link PersistableEntity} to which this
     * entity is subscribed or related.
     *
     * <p>
     * Note: In most cases, this {@link java.util.Optional} holds a non-null parent;
     * however, if the parent has been deleted, the subscription may persist and the result may be empty.
     *
     * @return a {@link Optional} containing the parent entity of type {@code T} if it exists,
     * or an empty {@link Optional} if no parent entity is associated; never null
     */
    @NonNull Optional<T> parent();

    /**
     * Retrieves the unique identifier of the parent entity associated with this subscription entity.
     *
     * @return the identifier of the parent entity of type {@code X}; never null
     */
    @NonNull X parentId();

    /**
     * Computes a derived signature of the associated parent entity's state.
     * This can be used to track changes or to verify the consistency between
     * the subscription entity and its parent.
     *
     * @return an int representing the calculated state signature of the parent entity
     * if the parent does not exist it will return 0.
     */
    int parentSignature();

}
