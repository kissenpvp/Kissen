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
