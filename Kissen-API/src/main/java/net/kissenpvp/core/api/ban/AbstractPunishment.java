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

import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.message.Comment;
import net.kissenpvp.core.api.networking.client.entitiy.OnlinePlayerClient;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.api.time.TemporalObject;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * This interface represents a player ban and provides methods to access and modify its properties.
 * A player ban is an action taken against a player who has violated a server rule or policy.
 * A player who is banned is prevented from accessing the server or chat for a specified amount of time or indefinitely, depending on the severity of the violation.
 * <p>
 * This interface provides methods to access and modify the properties of a player ban, such as the duration of the ban, the reason for the ban, the players affected by the ban, and the type of the ban.
 * It also provides methods to check whether a ban is currently valid and to retrieve a list of online players affected by the ban.
 * <p>
 * Implementations of this interface should be immutable, meaning that once a player ban has been created, its properties are not affected by changes made to its origin {@link AbstractBan} template.
 *
 * @see AbstractBan
 * @see AbstractBanImplementation
 * @see BanType
 */
public interface AbstractPunishment<T> extends TemporalObject {

    /**
     * Returns the total ID that clusters punishments together.
     * <p>
     * This ID can change if the system recognizes similarities between players IP addresses.
     * If they are recognized as related, they will have the same {@link PlayerClient#getTotalID()} and will be punished as if they were the same {@link PlayerClient}.
     *
     * @return the total ID assigned to this punishment.
     * @see PlayerClient#getTotalID()
     */
    @NotNull
    UUID getTotalID();

    /**
     * Returns a unique id.
     * <p>
     * This method returns a unique id, which is used to identify this punishment.
     * This id is generated when the punishment is created.
     *
     * @return the id of this specific punishment.
     */
    @NotNull
    String getID();

    /**
     * Returns the name of the ban template, portrayed by {@link AbstractBan}.
     * <p>
     * This method returns the name of the {@link AbstractBan} that was used to apply this punishment.
     * The name of the ban is kept even if it is overridden or deleted.
     *
     * @return the name of the ban associated with this player ban.
     */
    @NotNull
    String getName();


    /**
     * Returns the name of the player who created the punishment.
     * <p>
     * This method returns the name of the operator who applied this punishment.
     * As this saves the uuid of an operator if it is a player, it will keep track of their name.
     *
     * @return the current name of the operator.
     */
    @NotNull
    String getBanOperator();

    /**
     * Returns the ban type of the ban template, portrayed by {@link AbstractBan}.
     * <p>
     * This method returns the ban type of the {@link AbstractBan} that was used to apply this punishment.
     * The ban type of the ban is kept even if it is overridden or deleted.
     *
     * @return the type of the ban, as an enum constant of {@link BanType}
     */
    @NotNull
    BanType getBanType();

    /**
     * Returns an {@link Optional} containing the reason for the punishment, if it was specified when the ban was created.
     * <p>
     * If a reason was specified when the ban was created, it will be returned in the {@link Optional}.
     * If no reason was specified, an empty {@link Optional} will be returned.
     * <p>
     * Note that the reason for the punishment may be changed using the {@link #setCause(Component)} method.
     *
     * @return an {@link Optional} containing the reason for the punishment, or an empty {@link Optional} if no reason was specified
     * @see #setCause(Component)
     */
    @NotNull
    Optional<Component> getCause();

    /**
     * Sets the cause for the punishment.
     * <p>
     * This method sets the cause of this punishment. If the reason is {@code null}, the punishment won't have a cause anymore.
     * The cause should be a concise and clear explanation of the ban, and it can be retrieved by calling {@link #getCause()}.
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
    @NotNull
    @Unmodifiable
    List<Comment> getComments();

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
    @NotNull
    Comment addComment(@NotNull ServerEntity sender, @NotNull Component comment) throws EventCancelledException;

    /**
     * Retrieves a {@link Set} of {@link UUID}s that represent the players who are affected by this ban from the database. Use this method with caution, as it may take significant performance to load all affected players.
     * <p>
     * Note that players who have been affected by the ban but were not present in the database at the time of the ban (e.g. newly registered players) will not be included in this set.
     * <p>
     * The returned set is unmodifiable, meaning that attempts to modify the set (e.g. by adding or removing elements) will result in an {@link UnsupportedOperationException}.
     *
     * @return a {@link Set} of {@link UUID}s representing the players affected by the ban
     */
    @Unmodifiable
    Set<UUID> getAffectedPlayers();

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
    @Unmodifiable
    Set<T> getOnlineAffectedPlayers();

    /**
     * Retrieves a Translatable message associated with the user's punishment.
     * <p>
     * This method generates a Translatable instance that serves as a message corresponding to the
     * user's punishment. The message is used as a part of the banishment process to provide
     * information to the user about the punishment.
     *
     * @return A Translatable instance that represents a punishment message intended for the user.
     */
    @NotNull
    Component getPunishmentText(@NotNull Locale locale);
}
