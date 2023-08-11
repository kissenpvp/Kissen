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
