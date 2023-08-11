/*
 * Copyright 2023 KissenPvP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.kissenpvp.core.api.ban;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Locale;

/**
 * An enumeration of the different types of bans that can be applied to a player.
 * <p>
 * This enum contains three types of bans:
 * </p>
 * <ul>
 *   <li>{@link BanType#BAN} - a ban that kicks the player and prevents them from joining again</li>
 *   <li>{@link BanType#MUTE} - a mute that disallows the player from sending messages in chat</li>
 *   <li>{@link BanType#KICK} - a disconnect for the player from the server</li>
 * </ul>
 * Use the {@link BanType#toString()} method to obtain a string representation of a {@link BanType} object.
 * <p>
 * This enum is serializable, meaning that it can be stored or transferred as a sequence of bytes.
 * </p>
 *
 * @see Ban
 * @see PlayerBan
 */
public enum BanType implements Serializable
{
    BAN,
    MUTE,
    KICK;

    @Override public @NotNull String toString()
    {
        return name().toLowerCase(Locale.ENGLISH);
    }
}