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
