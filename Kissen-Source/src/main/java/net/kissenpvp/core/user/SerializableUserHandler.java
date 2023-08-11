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
import java.util.UUID;

public class SerializableUserHandler implements SerializableSavableHandler {
    public final UUID UUID;

    public SerializableUserHandler(java.util.UUID uuid) {
        this.UUID = uuid;
    }

    @Override
    public Savable getSavable() {
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
    public void create(@NotNull String name, @NotNull Map<String, String> data) {
        /* ignored */
    }

    @Override
    public void delete() throws BackendException {
        ((KissenSavable) getSavable()).softDelete();
    }
}
