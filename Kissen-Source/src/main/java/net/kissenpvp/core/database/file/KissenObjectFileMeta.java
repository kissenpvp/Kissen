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

package net.kissenpvp.core.database.file;

import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.ObjectMeta;
import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.api.database.savable.SavableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class KissenObjectFileMeta extends KissenFileMeta implements ObjectMeta {

    //TODO
    public KissenObjectFileMeta(@NotNull String file) {
        super(file);
    }

    @Override
    public void insertJsonMap(@NotNull String id, @NotNull Map<@NotNull String, @NotNull Object> data) throws BackendException {

    }

    @Override
    public @NotNull CompletableFuture<SavableMap> getData(@NotNull String totalId) throws BackendException {
        return CompletableFuture.failedFuture(new UnsupportedOperationException());
    }

    @Override
    public @Unmodifiable @NotNull <T extends Savable> CompletableFuture<Map<@NotNull String, @NotNull SavableMap>> getData(@NotNull T savable) throws BackendException {
        return CompletableFuture.failedFuture(new UnsupportedOperationException());
    }
}
