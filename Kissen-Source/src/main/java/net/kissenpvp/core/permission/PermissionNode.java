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

import net.kissenpvp.core.api.permission.AbstractPermission;
import net.kissenpvp.core.api.permission.AbstractPermissionEntry;
import net.kissenpvp.core.api.time.AccurateDuration;
import net.kissenpvp.core.api.time.TemporalObject;
import net.kissenpvp.core.api.util.Container;
import net.kissenpvp.core.api.time.TemporalData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This is a record class named {@code KissenPermissionNode} used for storing and handling data related to a Permission Entity.
 * <p>
 * The {@code KissenPermissionNode} record encapsulates five different attributes:
 * <ul>
 *  <li>{@code name}: a {@code NotNull String} value representing the name of a permission entity.</li>
 *  <li>{@code owner}: a {@code NotNull String} value representing the owner of a permission entity.</li>
 *  <li>{@code value}: a {@code NotNull Container} encapsulating a {@code NotNull Boolean} value that represents the value of the permission entity.</li>
 *  <li>{@code additionalData}: a {@code NotNull Map} representing additional data related to the permission entity.</li>
 *  <li>{@code temporalNode}: a {@code NotNull TemporalNode} object representing the temporal aspect associated with the permission entity.</li>
 * </ul>
 */
public record PermissionNode(@NotNull String name, @NotNull String owner, @NotNull Container<@NotNull Boolean> value,
                             @NotNull Map<String, String> additionalData, @NotNull TemporalData temporalData)
{
    /**
     * This constructor creates a new instance of {@code KissenPermissionNode} using an instance of {@code Permission}.
     *
     * @param permission a {@code Permission} object that contains {@code name}, {@code owner}, {@code value}, and {@code definedOptions}.
     * @throws NullPointerException if {@code permission} is {@code null}.
     */
    public PermissionNode(@NotNull AbstractPermission permission) {
        this(permission.getName(), permission.getOwner(), permission.getValue(), permission.getDefinedOptions(), permission);
    }

    /**
     * This constructor creates an instance of {@code KissenPermissionNode} using a series of parameters along with a {@code TemporalNode}.
     *
     * @param name  the name of the permission.
     * @param owner the owner of the permission, represented as a {@code PermissionEntry}.
     * @param value the value of the permission, represented as a boolean.
     * @param additionalData a {@code Map} of additional data related to the permission.
     * @param temporalData a {@code TemporalNode} object representing the temporal aspect associated with the permission.
     * @throws NullPointerException if any of {@code name}, {@code owner}, {@code additionalData} or {@code temporalNode} is {@code null}.
     */
    public PermissionNode(@NotNull String name, @NotNull AbstractPermissionEntry<?> owner, boolean value, @NotNull Map<String, String> additionalData, @NotNull TemporalData temporalData) {
        this(name, owner.getPermissionID(), new Container<>(value), additionalData, temporalData);
    }

    /**
     * This constructor creates an instance of {@code KissenPermissionNode} using a series of parameters along with a {@code TemporalObject}.
     *
     * @param name  the name of the permission.
     * @param owner the owner of the permission, represented as a {@code PermissionEntry}.
     * @param value the value of the permission, represented as a boolean.
     * @param additionalData a {@code Map} of additional data related to the permission.
     * @param temporalObject a {@code TemporalObject} object representing the temporal aspect associated with the permission. It will be converted into a {@code TemporalNode} object.
     * @throws NullPointerException if any of {@code name}, {@code owner}, {@code additionalData} or {@code temporalObject} is {@code null}.
     */
    public PermissionNode(@NotNull String name, @NotNull AbstractPermissionEntry<?> owner, boolean value, @NotNull Map<String, String> additionalData, @NotNull TemporalObject temporalObject) {
        this(name, owner.getPermissionID(), new Container<>(value), additionalData, new TemporalData(temporalObject));
    }

    /**
     * This constructor creates an instance of {@code KissenPermissionNode} with a minimal set of required parameters along with a {@code TemporalObject}, the additional data is initialized to an empty {@code HashMap}.
     *
     * @param name  the name of the permission.
     * @param owner the owner of the permission, represented as a {@code PermissionEntry}.
     * @param value the value of the permission, represented as a boolean.
     * @param temporalObject a {@code TemporalObject} object representing the temporal aspect associated with the permission. It will be converted into a {@code TemporalNode} object.
     */
    public PermissionNode(String name, AbstractPermissionEntry<?> owner, boolean value, @NotNull TemporalObject temporalObject) {
        this(name, owner, value, new HashMap<>(), temporalObject);
    }

    /**
     * This constructor creates an instance of {@code KissenPermissionNode} with a minimal set of required parameters along with a {@code TemporalNode}, the additional data is initialized to an empty {@code HashMap}.
     *
     * @param name  the name of the permission.
     * @param owner the owner of the permission, represented as a {@code PermissionEntry}.
     * @param value the value of the permission, represented as a boolean.
     * @param temporalData a {@code TemporalNode} object representing the temporal aspect associated with the permission.
     */
    public PermissionNode(String name, AbstractPermissionEntry<?> owner, boolean value, @NotNull TemporalData temporalData) {
        this(name, owner.getPermissionID(), new Container<>(value), new HashMap<>(), temporalData);
    }

    /**
     * This constructor creates an instance of {@code KissenPermissionNode} with a minimal set of required parameters and a duration, represented as a {@code PeriodDuration}. The additional data is initialized to an empty {@code HashMap}. A new {@code TemporalNode} is created with the start expire as the current system expire and the end expire as the start expire plus the duration.
     *
     * @param name  the name of the permission.
     * @param owner the owner of the permission, represented as a {@code PermissionEntry}.
     * @param value the value of the permission, represented as a boolean.
     * @param accurateDuration the duration of the period, represented as a {@code Nullable PeriodDuration}.
     */
    public PermissionNode(String name, AbstractPermissionEntry<?> owner, boolean value, @Nullable AccurateDuration accurateDuration) {
        this(name, owner.getPermissionID(), new Container<>(value), new HashMap<>(), new TemporalData(System.currentTimeMillis(), accurateDuration));
    }

    @Override public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof PermissionNode that))
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
