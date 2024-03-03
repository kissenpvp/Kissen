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

import net.kissenpvp.core.api.ban.Ban;
import net.kissenpvp.core.api.ban.BanType;
import net.kissenpvp.core.api.database.DataImplementation;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.api.util.Container;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.message.CommentNode;
import net.kissenpvp.core.time.TemporalMeasureNode;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The {@link KissenPunishmentNode} record encapsulates all the information necessary to deal with the punishments in the form of ban.
 * A {@link KissenPunishmentNode} instance describes the punishment with attributes like id, ban name, operator, ban type, etc.
 * <p>
 * This record creates getters for all declared attributes and attractively packages ban related data.
 *
 * @param id The identifier for the punishment record, this parameter must not be null
 * @param banName The name of the ban, this parameter must not be null
 * @param operator The operator or the agent who executed the ban, this parameter must not be null
 * @param banType The type of the ban, this parameter must not be null
 * @param cause The reason for this punishment, this parameter can be null inside the container
 * @param comments Additional comments for this punishment, this parameter must not be null
 * @param temporalMeasureNode The expire bound element of the punishment, this parameter must not be null
 *
 * @see Record
 * @see NotNull
 */
public record KissenPunishmentNode(@NotNull String id, @NotNull String banName, @NotNull String operator,
                                   @NotNull BanType banType, @NotNull Container<Component> cause,
                                   @NotNull List<CommentNode> comments, @NotNull TemporalMeasureNode temporalMeasure)
{

    public KissenPunishmentNode(@NotNull Ban ban, @NotNull ServerEntity banOperator, @Nullable Component reason) {
        this(KissenCore.getInstance().getImplementation(DataImplementation.class).generateID(), ban.getName(), banOperator instanceof PlayerClient<?,?,?> playerClient ? playerClient.getUniqueId().toString() : banOperator.getName(), ban.getBanType(), new Container<>(reason), new ArrayList<>(), new TemporalMeasureNode(System.currentTimeMillis(), ban.getAccurateDuration().orElse(null)));
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "KissenPunishmentNode{" +
                "id='" + id + '\'' +
                ", banName='" + banName + '\'' +
                ", operator='" + operator + '\'' +
                ", banType=" + banType +
                ", cause=" + cause +
                ", comments=" + comments +
                ", temporalMeasure=" + temporalMeasure +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KissenPunishmentNode that = (KissenPunishmentNode) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
