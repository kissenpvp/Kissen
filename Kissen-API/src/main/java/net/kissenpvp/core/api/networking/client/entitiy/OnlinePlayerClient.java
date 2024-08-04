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

package net.kissenpvp.core.api.networking.client.entitiy;

import io.netty.channel.Channel;
import net.kissenpvp.core.api.ban.AbstractPunishment;
import net.kissenpvp.core.api.base.KissenServer;
import net.kissenpvp.core.api.permission.AbstractPermission;
import net.kissenpvp.core.api.user.rank.AbstractPlayerRank;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface OnlinePlayerClient<P extends AbstractPermission, R extends AbstractPlayerRank<?>, B extends AbstractPunishment<?>> extends PlayerClient<R, B>, MessageReceiver {

    void kick(@NotNull String message);

    void kick(@NotNull Component component);

    void killConnection();

    long getLastLogin();

    @NotNull Channel getConnection();

    void sendPacket(@NotNull Object object) throws ClassNotFoundException, ClassCastException;

    @Nullable KissenServer getCurrentServer();
}
