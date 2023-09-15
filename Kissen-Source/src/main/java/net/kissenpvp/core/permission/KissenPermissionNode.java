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

package net.kissenpvp.core.permission;

import net.kissenpvp.core.api.util.Container;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;


public record KissenPermissionNode(String name, String owner, Container<Boolean> value, long start,
                                   Container<Long> duration, Container<Long> end, long predictedEnd,
                                   @NotNull Map<String, String> additionalData)
{
    @Override public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof KissenPermissionNode that))
        {
            return false;
        }
        return Objects.equals(name, that.name) && Objects.equals(owner, that.owner);
    }

    @Override public int hashCode()
    {
        return Objects.hash(name, owner);
    }
}
