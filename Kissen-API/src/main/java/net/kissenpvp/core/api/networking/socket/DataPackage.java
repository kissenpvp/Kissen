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

package net.kissenpvp.core.api.networking.socket;

import java.io.Serializable;
import java.util.List;


public interface DataPackage extends List<Serializable>
{
    /**
     * The ID or the execution command.
     *
     * @return The id of the data packet.
     */
    String getID();

    String getSender();

    String getGroup();

    boolean isReply();

    void setReply(boolean reply);

    /**
     * Executed by clients to indicate who the message is coming from.
     * If the package is not signed, the server will refuse the answer.
     *
     * @param sender The sender of the package.
     * @param group  The related group.
     */
    void sign(String sender, String group);
}
