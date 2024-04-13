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
import net.kissenpvp.core.api.user.rank.AbstractRankImplementation;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.savable.KissenSavable;
import net.kissenpvp.core.database.savable.SerializableSavableHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;


public class SerializableRankHandler implements SerializableSavableHandler {
    private final String rankName;

    public SerializableRankHandler(String rankName) {
        this.rankName = rankName;
    }

    @Override
    public Savable getSavable() {
        return (Savable) KissenCore.getInstance().getImplementation(AbstractRankImplementation.class).getRank(rankName).orElse(null);
    }

    @Override
    public void create(@NotNull String name, @NotNull Map<String, String> data) {
        KissenCore.getInstance().getImplementation(AbstractRankImplementation.class).createRank(name, data);
    }

    @Override
    public void set(String key, String value) {
        //getSavable().put(key, value);
    }

    @Override
    public void setList(String key, List<String> value) {
        //getSavable().putList(key, value);
    }

    @Override
    public void delete() throws BackendException {
        ((KissenSavable) getSavable()).softDelete();
    }
}
