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

package net.kissenpvp.core.database.savable.list;

import net.kissenpvp.core.api.database.DataImplementation;
import net.kissenpvp.core.api.database.savable.list.ListAction;
import net.kissenpvp.core.api.database.savable.list.ListExecution;
import net.kissenpvp.core.api.database.savable.list.SavableRecordList;
import net.kissenpvp.core.base.KissenCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class KissenSavableRecordList<T extends Record> extends KissenSavableList implements SavableRecordList<T> {
    private final Class<T> record;

    public KissenSavableRecordList(KissenSavableList parent, Class<T> record) {
        this.record = record;
        addAll(parent);
        setListAction((listExecution, values) -> parent.clearAndAddAll(KissenSavableRecordList.this));
    }

    @Override
    public boolean add(@NotNull T element) {
        return super.add(KissenCore.getInstance().getImplementation(DataImplementation.class).toJson(element));
    }

    @Override
    public void add(int index, @NotNull T element) {
        super.add(index, KissenCore.getInstance().getImplementation(DataImplementation.class).toJson(element));
    }

    @Override
    public boolean addAllRecord(@NotNull Collection<T> elements) {
        return super.addAll(elements.stream().map(record -> KissenCore.getInstance().getImplementation(DataImplementation.class).toJson(record)).collect(Collectors.toSet()));
    }

    @Override
    public boolean addAllRecord(int index, @NotNull Collection<T> elements) {
        return super.addAll(index,
                elements.stream().map(record -> KissenCore.getInstance().getImplementation(DataImplementation.class).toJson(record)).collect(Collectors.toSet()));
    }

    @Override
    public boolean remove(@NotNull T element) {
        return super.remove(KissenCore.getInstance().getImplementation(DataImplementation.class).toJson(element));
    }

    @Override
    public boolean removeAllRecord(@NotNull Collection<T> elements) {
        return super.removeAll(elements.stream().map(record -> KissenCore.getInstance().getImplementation(DataImplementation.class).toJson(record)).collect(Collectors.toSet()));
    }

    @Override
    public boolean removeIfRecord(@NotNull Predicate<? super T> filter) {
        return this.removeAll(toRecordList().stream().filter(filter).map(record -> KissenCore.getInstance().getImplementation(DataImplementation.class).toJson(record)).toList());
    }

    @Override
    public int replaceRecord(@NotNull Predicate<T> predicate, @NotNull T element) {
        final AtomicInteger count = new AtomicInteger(0);

        List<T> newList = new ArrayList<>();
        this.stream().map(entry -> KissenCore.getInstance().getImplementation(DataImplementation.class).fromJson(entry, element.getClass())).forEach(entry ->
        {
            if (predicate.test((T) entry)) {
                newList.add(element);
                count.addAndGet(1);
                return;
            }
            newList.add((T) entry);
        });

        ListAction listAction = super.getListAction().orElse(null);
        super.setListAction(null);
        super.clear();
        super.addAll(newList.stream().map(recordEntry -> KissenCore.getInstance().getImplementation(DataImplementation.class).toJson(recordEntry)).toList());
        super.setListAction(listAction);
        return parseValue(count::get,
                () -> getListAction().ifPresent(action -> action.execute(ListExecution.REPLACE_ALL, predicate,
                        element)));
    }

    @Override
    public boolean contains(@NotNull T element) {
        return stream().anyMatch(currentTest -> KissenCore.getInstance().getImplementation(DataImplementation.class).fromJson(currentTest, record).equals(element));
    }

    @Override
    public @NotNull @Unmodifiable List<T> toRecordList() {
        return stream().map(entry -> KissenCore.getInstance().getImplementation(DataImplementation.class).fromJson(entry, record)).toList();
    }
}
