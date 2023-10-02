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

package net.kissenpvp.core.user;

import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.api.user.UserImplementation;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.savable.KissenSavable;
import net.kissenpvp.core.database.savable.SerializableSavableHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents the {@link SerializableUserHandler} class that implements {@link SerializableSavableHandler} interface.
 * This class provides a mechanism to manipulate users' data and makes it serializable, for data saving purposes.
 * <p>
 * The {@link SerializableUserHandler} class has a unique user identifier (UUID) which is used to recognize each
 * individual user.
 * <p>
 * This class is designed to function as an intermediary between the application and the underlying serialization system,
 * and it ensures unique identification for serialized user's data.
 * <p>
 * This is typically a universally unique identifier (UUID).
 */
public class SerializableUserHandler implements SerializableSavableHandler {

    public final UUID UUID;

    /**
     * Constructs an instance of the {@link SerializableUserHandler} class using a unique identifier (UUID),
     * assigning the provided UUID to the class's UUID field.
     * <p>
     * The UUID provided should be non-null and unique for each {@link SerializableUserHandler} instance to ensure accurate serialization and saving.
     *
     * @param uuid the unique identifier to be used for this instance of {@link SerializableUserHandler}. Must be non-null.
     * @throws IllegalArgumentException If the uuid is null.
     */
    public SerializableUserHandler(@NotNull UUID uuid) {
        this.UUID = uuid;
    }

    @Override
    public @NotNull Savable getSavable() {
        return KissenCore.getInstance().getImplementation(UserImplementation.class).getUser(UUID);
    }

    @Override
    public void set(String key, String value) {
        getSavable().put(key, value);
    }

    @Override
    public void setList(String key, List<String> value) {
        getSavable().putList(key, value);
    }

    @Override
    public void create(@NotNull String name, @NotNull Map<String, String> data) { /* ignored */ }

    @Override
    public void delete() throws BackendException {
        ((KissenSavable) getSavable()).softDelete();
    }
}
