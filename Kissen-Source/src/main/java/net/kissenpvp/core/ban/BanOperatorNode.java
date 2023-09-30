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

package net.kissenpvp.core.ban;

import net.kissenpvp.core.api.ban.BanOperator;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.message.KissenComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * The {@link BanOperatorNode} record encapsulates all information necessary for an operator who executes a Ban.
 * A {@link BanOperatorNode} instance represents a ban operator with a UUID and a name.
 * The UUID may be null in case the {@link BanOperator} is not a {@link PlayerClient}.
 * <p>
 * This record creates getters for the declared UUID and name and provides additional methods.
 *
 * @param uuid The unique identifier for the ban operator, can be null
 * @param name The name of the ban operator represented as serialized JSON string, cannot be null
 *
 * @see Record
 * @see PlayerClient
 * @see Nullable
 * @see NotNull
 */
public record BanOperatorNode(@Nullable UUID uuid, @NotNull String name)
{

    /**
     * Constructs a new {@link BanOperatorNode} from a given {@link BanOperator}.
     * The UUID is set to null, the name is obtained from the serialized string of the operator's display name.
     *
     * @param banOperator The {@link BanOperator} to be converted to a node, cannot be null
     *
     * @see KissenComponentSerializer
     * @see JSONComponentSerializer
     */
    public BanOperatorNode(@NotNull BanOperator banOperator)
    {
        this(null, JSONComponentSerializer.json().serialize(banOperator.displayName()));
    }

    /**
     * Returns the display name of the ban operator as a {@link Component}.
     * This is obtained by deserializing the JSON string representation of the name.
     *
     * @return The display name of the operator
     *
     * @see JSONComponentSerializer
     */
    public @NotNull Component displayName() {
        return KissenComponentSerializer.getInstance().getJsonSerializer().deserialize(name());
    }
}
