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

package net.kissenpvp.core.networking.socket;

import lombok.Getter;
import lombok.Setter;
import net.kissenpvp.core.api.networking.socket.DataPackage;
import net.kissenpvp.core.api.networking.socket.InternalProtocols;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;


public class KissenDataPackage extends ArrayList<Serializable> implements DataPackage
{
    @Getter @Setter private boolean reply;
    @Getter private String sender, group;

    /**
     * Creates a data packet.
     * Which also specifies whether an answer is expected. This option is <code>true</code> by default.
     *
     * @param id    The ID of the data packet.
     * @param input The data that will be sent with (optional)
     */
    public KissenDataPackage(@NotNull String id, @NotNull Serializable... input)
    {
        this.reply = true;
        super.add(0, id);
        this.addAll(Arrays.asList(input));
    }


    @Override public String getID()
    {
        if (!super.isEmpty() && !(super.get(0) instanceof String))
        {
            return InternalProtocols.INVALID.toString();
        }
        return String.valueOf(super.get(0));
    }

    @Override public void sign(String sender, String group)
    {
        this.sender = sender;
        this.group  = group;
    }

    @Override public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        this.forEach(data ->
        {
            if (data == null)
            {
                stringBuilder.append("null");
            }
            else
            {
                stringBuilder.append(data).append(" [").append(data.getClass()).append("]");
            }
            stringBuilder.append(", ");
        });
        String data = stringBuilder.toString().trim();
        return "DataPackage{sender='" + get(0) + '\'' + ", sender='" + sender + '\'' + ", group='" + group + '\'' + '}';
    }
}
