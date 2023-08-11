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

package net.kissenpvp.core.user.rank;

import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.api.user.rank.RankImplementation;
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
        return (Savable) KissenCore.getInstance().getImplementation(RankImplementation.class).getRank(rankName).orElse(null);
    }

    @Override
    public void create(@NotNull String name, @NotNull Map<String, String> data) {
        KissenCore.getInstance().getImplementation(RankImplementation.class).createRank(name, data);
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
    public void delete() throws BackendException {
        ((KissenSavable) getSavable()).softDelete();
    }
}
