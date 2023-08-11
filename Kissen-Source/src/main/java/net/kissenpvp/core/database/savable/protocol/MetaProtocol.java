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

package net.kissenpvp.core.database.savable.protocol;

import net.kissenpvp.core.api.networking.socket.DataPackage;
import net.kissenpvp.core.api.networking.socket.Execution;
import net.kissenpvp.core.database.savable.SerializableSavableHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.net.Socket;


public abstract class MetaProtocol implements Execution
{
    @Override public final @Nullable Serializable @Nullable [] execute(@NotNull DataPackage dataPackage, @NotNull Socket client)
    {
        SerializableSavableHandler savableHandler = ((SerializableSavableHandler) dataPackage.get(1));
        if (savableHandler != null)
        {
            return execute(savableHandler, dataPackage, client);
        }

        return getDefaultResponse();
    }

    /**
     * is executed with the savable as an additional argument.
     *
     * @param savable     the savable this protocol is about.
     * @param dataPackage the data this which is send with the request.
     * @param client      the temporary socket client this request comes from.
     * @return the response to the sender.
     */
    public abstract Serializable[] execute(SerializableSavableHandler savable, DataPackage dataPackage, Socket client);

    /**
     * @return the response which is sent when the savable is null.
     */
    public Serializable[] getDefaultResponse()
    {
        return new Serializable[0];
    }
}
