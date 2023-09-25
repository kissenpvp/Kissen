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

package net.kissenpvp.core.api.ban;

import net.kissenpvp.core.api.database.DataImplementation;
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.message.Comment;
import net.kissenpvp.core.api.networking.client.entitiy.OnlinePlayerClient;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.api.time.TemporalObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.translation.Translatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * This interface represents a player ban and provides methods to access and modify its properties.
 * A player ban is an action taken against a player who has violated a server rule or policy.
 * A player who is banned is prevented from accessing the server or chat for a specified amount of time or indefinitely, depending on the severity of the violation.
 * <p>
 * This interface provides methods to access and modify the properties of a player ban, such as the duration of the ban, the reason for the ban, the players affected by the ban, and the type of the ban.
 * It also provides methods to check whether a ban is currently valid and to retrieve a list of online players affected by the ban.
 * <p>
 * Implementations of this interface should be immutable, meaning that once a player ban has been created, its properties are not affected by changes made to its origin {@link Ban} template.
 *
 * @see Ban
 * @see BanImplementation
 * @see BanType
 */
public interface Punishment<T> extends TemporalObject {

    /**
     * Returns the total ID that is assigned to a player ban. This ID can change if the system recognizes similarities
     * between players' IP addresses. If they are recognized as related, they will have the same total ID and will be
     * punished as if they were the same player.
     *
     * @return the total ID assigned to a player ban
     */
    @NotNull UUID getTotalID();

    /**
     * Returns a unique string identifier that distinguishes the bans associated with a particular total id.
     * In order to generate this id, {@link DataImplementation#generateID()} is used.
     * <p>
     * The ID has the following format: a four-character sequence consisting of lowercase letters and numbers. For example: "a784, 1cdf, aa82".
     * It matches the regular expression pattern: {@code ^[a-z0-9]{4}$}.
     * <p>
     * The ID is immutable and cannot be changed.
     *
     * @return the unique identifier for this player ban, following the format "^[a-z0-9]{4}$"
     * @throws IllegalArgumentException if the ID does not match the expected format
     */
    @NotNull String getID();

    /**
     * Returns the name of the ban that was used to apply this punishment.
     * The name of the ban is kept even if it is overridden or deleted.
     *
     * @return the name of the ban associated with this player ban
     */
    @NotNull String getName();

    /**
     * Returns the name of the team member who applied this punishment.
     * This can be used for accountability purposes and to identify who applied the ban.
     *
     * @return the name of the team member who applied this player ban
     */
    @NotNull BanOperator getBanOperator();

    /**
     * Returns the type of the ban associated with this player ban.
     * The ban type of the {@link Ban} is kept even if it is overridden or deleted.
     *
     * @return the type of the ban, as an enum constant of {@link BanType}
     */
    @NotNull BanType getBanType();

    /**
     * Returns an {@link Optional} containing the reason for the punishment, if it was specified when the ban was created.
     *
     * <p>If a reason was specified when the ban was created, it will be returned in the {@link Optional}.
     * If no reason was specified, an empty {@link Optional} will be returned.</p>
     *
     * <p>Note that the reason for the punishment may be changed using the {@link #setCause(Component)} method.</p>
     *
     * @return an {@link Optional} containing the reason for the punishment, or an empty {@link Optional} if no reason was specified
     */
    @NotNull Optional<Component> getCause();

    /**
     * Sets the reason for the player's ban. If the reason is null, the ban will be set without a reason.
     * The reason should be a concise and clear explanation of the ban, and it can be retrieved by calling {@link #getCause()}.
     *
     * @param cause the reason for the ban, or null to remove the reason.
     * @see #getCause()
     */
    void setCause(@Nullable Component cause) throws EventCancelledException;

    /**
     * Returns an unmodifiable list of comments made by team members on this player ban.
     * <p>
     * If there are no comments, an empty list is returned. The list is unmodifiable,
     * meaning that it cannot be altered directly. To add comments, use the
     * {@link #addComment(ServerEntity, Component)} method, which creates a new
     * {@link Comment} object representing the comment made by the sender.
     *
     * @return an unmodifiable list of {@link Comment} objects made by team members
     * on this player ban, or an empty list if there are no comments.
     * @see #addComment(ServerEntity, Component)
     * @see Comment
     */
    @NotNull @Unmodifiable List<Comment> getComments();

    /**
     * Adds a new comment to this player ban by creating a new {@link Comment} object.
     * <p>
     * The comment will contain the name of the sender and the message. The comments are stored and can be retrieved
     * using the {@link #getComments()} method.
     *
     * @param sender  the {@link ServerEntity} object representing the person who is adding the comment
     * @param comment the message to add as a comment
     * @return the newly created {@link Comment} object representing the added comment
     * @see #getComments()
     * @see Comment
     */
    @NotNull Comment addComment(@NotNull ServerEntity sender, @NotNull Component comment) throws EventCancelledException;

    /**
     * Retrieves a {@link Set} of {@link UUID}s that represent the players who are affected by this ban from the database. Use this method with caution, as it may take significant performance to load all affected players.
     * <p>
     * Note that players who have been affected by the ban but were not present in the database at the time of the ban (e.g. newly registered players) will not be included in this set.
     * <p>
     * The returned set is unmodifiable, meaning that attempts to modify the set (e.g. by adding or removing elements) will result in an {@link UnsupportedOperationException}.
     *
     * @return a {@link Set} of {@link UUID}s representing the players affected by the ban
     */
    @Unmodifiable Set<UUID> getAffectedPlayers();

    /**
     * Retrieves a {@link Set} of {@link OnlinePlayerClient}s that represent the online players who are affected by this ban.
     * This method takes less performance than {@link #getAffectedPlayers()} because it only includes online players.
     * <p>
     * Note also that players who have been affected by the ban but are not currently online will not be included in this set.
     * <p>
     * The returned set is unmodifiable, meaning that attempts to modify the set (e.g. by adding or removing elements) will result in an {@link UnsupportedOperationException}.a.
     *
     * @return a {@link Set} of {@link PlayerClient}s representing the online players affected by the ban
     */
    @Unmodifiable Set<T> getOnlineAffectedPlayers();

    /**
     * Retrieves a Translatable message associated with the user's punishment.
     * <p>
     * This method generates a Translatable instance that serves as a message corresponding to the
     * user's punishment. The message is used as a part of the banishment process to provide
     * information to the user about the punishment.
     *
     * @return A Translatable instance that represents a punishment message intended for the user.
     */
    @NotNull Component getPunishmentText(@NotNull Locale locale);
}
