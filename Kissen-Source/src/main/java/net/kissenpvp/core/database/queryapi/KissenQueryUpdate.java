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

package net.kissenpvp.core.database.queryapi;

import net.kissenpvp.core.api.database.queryapi.update.QueryUpdate;
import net.kissenpvp.core.api.database.queryapi.update.Update;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public abstract class KissenQueryUpdate extends KissenQueryComponent<QueryUpdate> implements QueryUpdate
{
    private final Update[] updates;

    public KissenQueryUpdate(Update... updates)
    {
        this.updates = updates;
    }

    @Override public Update[] getColumns()
    {
        return updates;
    }

    public abstract static class KissenRootQueryUpdate extends KissenRootQueryComponent<QueryUpdate> implements RootQueryUpdate {

        public KissenRootQueryUpdate(@NotNull Update... updates) {
            this.setQueryComponent(new KissenQueryUpdate(updates) {
                @Override
                public @NotNull CompletableFuture<Long> execute() {
                    return KissenRootQueryUpdate.this.execute();
                }
            });
        }
    }
}
