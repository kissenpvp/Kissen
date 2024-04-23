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

package net.kissenpvp.core.user.rank;

import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.api.user.rank.AbstractRank;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.savable.KissenSavable;
import net.kissenpvp.core.database.savable.SerializableSavableHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public abstract class KissenRank extends KissenSavable<String> implements AbstractRank {
    @Override
    public final @NotNull String getSaveID() {
        return "rank";
    }

    @Override
    public String[] getKeys() {
        return new String[]{"priority"};
    }

    @Override
    public @NotNull String getName() {
        return getRawID();
    }

    @Override
    public int getPriority() {
        return getRepository().getNotNull("priority", Integer.class);
    }

    @Override
    public void setPriority(int priority) {
        getRepository().set("priority", priority);
    }

    @Override
    public SerializableSavableHandler getSerializableSavableHandler() {
        return new SerializableRankHandler(this.getName());
    }

    @Override
    public int softDelete() throws BackendException {
        KissenCore.getInstance().getImplementation(KissenRankImplementation.class).removeRank(this);
        return super.softDelete();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDatabaseID(), getPriority());
    }
}
