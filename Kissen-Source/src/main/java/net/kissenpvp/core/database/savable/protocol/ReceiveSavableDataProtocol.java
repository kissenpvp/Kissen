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
import net.kissenpvp.core.api.networking.socket.Protocol;
import net.kissenpvp.core.database.savable.SerializableSavableHandler;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.net.Socket;


@Protocol("receive_savable_data")
public class ReceiveSavableDataProtocol extends MetaProtocol {
    @Override
    public Serializable[] execute(@NotNull SerializableSavableHandler savable, DataPackage dataPackage, Socket client) {
        return new Serializable[]
                {
                        savable.getSavable().serialize()
                };
    }

    @Override
    public Serializable[] getDefaultResponse() {
        return new Serializable[]{null};
    }
}
