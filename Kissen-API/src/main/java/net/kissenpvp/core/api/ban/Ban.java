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

import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.time.AccurateDuration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

/**
 * The {@code Ban} interface represents a ban that can be applied to a player in a game or server, specifying its unique ID, name, type, duration, and behavior.
 * <p>
 * To create a ban, you can use either {@link BanImplementation#createBan(int, String, BanType, net.kissenpvp.core.api.time.AccurateDuration)} or {@link BanImplementation#createBan(int, Map)}.
 * <p>
 * Once a ban has been created, it can be loaded using {@link BanImplementation#getBan(int)}.
 * <br>This allows you to manage bans for players, including specifying the ban's ID, name, type, duration, and behavior.
 * <p>
 * If you need to retrieve all bans at once, you can use {@link BanImplementation#getBanSet()}.
 * <p>
 * These Bans do NOT apply to player directly, instead they function as template for creating a {@link Punishment}.
 *
 * @see Punishment
 * @see BanType
 * @see BanImplementation
 */
public interface Ban {

    /**
     * Returns the unique ID that identifies this ban object. This ID cannot be edited once the object is created.
     * <p>
     * However, it is possible to create a new object with the same ID and add it to the system. This will delete the previously created instance of the {@link Ban}.
     * <p>
     * If this ID is no longer needed, call the {@link #delete()} method to remove it from the system.
     *
     * @return The unique ID that identifies this ban object.
     */
    int getID();

    /**
     * Returns the name of this ban as it appears to the player when they are banned.
     * <p>
     * Note that changes won't be applied to already created bans.
     * </p>
     *
     * @return The name of this ban.
     * @see #setName(String)
     */
    @NotNull String getName();

    /**
     * Sets the name of this ban as it appears to the player when they are banned.
     *
     * @param name The name of this ban.
     * @see #getName()
     */
    void setName(@NotNull String name) throws EventCancelledException;

    /**
     * Returns the type of this ban, which specifies what happens to the player when this ban is applied.
     *
     * @return The type of this ban.
     * @see #setBanType(BanType)
     */
    @NotNull BanType getBanType();

    /**
     * Sets the type of this ban, which specifies what happens to the player when this ban is applied.
     * <p>
     * Note that changes won't be applied to already created bans.
     * </p>
     *
     * @param banType The type of this ban.
     * @see #getBanType()
     */
    void setBanType(@NotNull BanType banType) throws EventCancelledException;

    /**
     * Returns the duration of this ban, which specifies how long the ban lasts.
     * If the ban is permanent, an empty {@link Optional} is returned.
     *
     * @return An {@link Optional} containing the duration of this ban, or empty if the ban is permanent.
     * @see #setAccurateDuration(AccurateDuration)
     */
    @NotNull Optional<AccurateDuration> getAccurateDuration();

    /**
     * Sets the duration of this ban, which specifies how long the ban lasts.
     * If the duration is null, the ban is considered permanent.
     * <p>
     * Note that changes won't be applied to already created bans.
     * </p>
     *
     * @param duration The duration of this ban, or null if the ban is permanent.
     * @see #getAccurateDuration()
     */
    void setAccurateDuration(@Nullable AccurateDuration duration) throws EventCancelledException;

    /**
     * Deletes this ban object from the system and returns the number of database entries that were deleted.
     *
     * @return The number of database entries that were deleted as a result of calling this method.
     */
    int delete() throws BackendException;

}