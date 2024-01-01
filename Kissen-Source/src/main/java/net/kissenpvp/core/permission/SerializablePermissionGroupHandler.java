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

import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.savable.SerializableSavableHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class SerializablePermissionGroupHandler implements SerializableSavableHandler {

    private final @NotNull String permissionGroup;

    public SerializablePermissionGroupHandler(@NotNull String permissionGroup) {
        this.permissionGroup = permissionGroup;
    }

    @Override
    public Savable getSavable() {
        return (Savable) KissenCore.getInstance()
                .getImplementation(PermissionImplementation.class)
                .getPermissionGroupSavable(permissionGroup).orElseThrow(NullPointerException::new);
    }

    @Override
    public void set(@NotNull String key, @Nullable String value) {
        getSavable().put(key, value);
    }

    @Override
    public void setList(@NotNull String key, @Nullable List<String> value) {
        getSavable().putList(key, value);
    }

    @Override
    public void create(@NotNull String name, @NotNull Map<String, String> data) {
        try {
            KissenCore.getInstance()
                    .getImplementation(PermissionImplementation.class)
                    .create(name, data);
        } catch (BackendException backendException) {
            KissenCore.getInstance()
                    .getLogger()
                    .error("The system was unable to create the permission group {} due to some backend issue. It is advised to shutdown the server to prevent data loss to the database.", name, backendException);
        }
    }

    @Override
    public void delete() {
        KissenCore.getInstance()
                .getImplementation(PermissionImplementation.class)
                .removePermissionGroup(permissionGroup);
    }
}
