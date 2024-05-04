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

package net.kissenpvp.core.database.savable.list;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kissenpvp.core.api.database.meta.list.ListAction;
import net.kissenpvp.core.api.database.meta.list.MetaList;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KissenMetaList<T> extends ArrayList<T> implements MetaList<T> {
    private ListAction<T> listAction;

    public KissenMetaList(int initialCapacity) {
        super(initialCapacity);
    }

    public KissenMetaList(@NotNull Collection<? extends T> c) {
        super(c);
    }

    public KissenMetaList(@NotNull Collection<? extends T> c, ListAction<T> listAction) {
        super(c);
        this.listAction = listAction;
    }

    @Override
    public @NotNull Optional<@NotNull ListAction<T>> getListAction() {
        return Optional.ofNullable(listAction);
    }

    @Override
    public T set(int index, T element) {
        return parseValue(() -> super.set(index, element), ListAction.ListExecutionType.SET);
    }

    @Override
    public boolean add(T t) {
        return parseValue(() -> super.add(t), ListAction.ListExecutionType.ADD);
    }

    @Override
    public void add(int index, T element) {
        parseValue(() -> {
            super.add(index, element);
            return false;
        }, ListAction.ListExecutionType.ADD_INDEX);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        return parseValue(() -> super.addAll(c.stream().toList()), ListAction.ListExecutionType.ADD_ALL);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        return parseValue(() -> super.addAll(index, c), ListAction.ListExecutionType.ADD_ALL_INDEX_INCLUDED);
    }

    @Override
    public T remove(int index) {
        return parseValue(() -> super.remove(index), ListAction.ListExecutionType.REMOVE_INDEX);
    }

    @Override
    public boolean remove(Object o) {
        return parseValue(() -> super.remove(o), ListAction.ListExecutionType.REMOVE);
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        parseValue(() -> {
            super.removeRange(fromIndex, toIndex);
            return false;
        }, ListAction.ListExecutionType.REMOVE_RANGE);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return parseValue(() -> super.removeAll(c), ListAction.ListExecutionType.REMOVE_ALL);
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        return parseValue(() -> super.removeIf(filter), ListAction.ListExecutionType.REMOVE_IF);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return parseValue(() -> super.retainAll(c), ListAction.ListExecutionType.RETAIN_ALL);
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator) {
        parseValue(() -> {
            super.replaceAll(operator);
            return true;
        }, ListAction.ListExecutionType.REPLACE_ALL);
    }

    @Override
    public void clear() {
        parseValue(() -> {
            super.clear();
            return true;
        }, ListAction.ListExecutionType.CLEAR);
    }

    @Override
    public boolean[] invert(@NotNull T value) {
        return !contains(value) ? new boolean[]{true, add(value)}:new boolean[]{false, remove(value)};
    }

    @Override
    public int replace(@NotNull T object) {
        return replace(current -> Objects.equals(object, current), object);
    }

    @Override
    public int replace(@NotNull Predicate<T> predicate, @NotNull T object) {
        return parseValue(() -> {
            AtomicInteger count = new AtomicInteger(0);

            List<T> newList = this.stream().map(entry -> {
                if (predicate.test(entry)) {
                    count.incrementAndGet();
                    return object;
                }
                return entry;
            }).toList();

            super.clear();
            super.addAll(newList);

            return count.get();
        }, ListAction.ListExecutionType.REPLACE);
    }

    @Override
    public boolean replaceOrInsert(@NotNull T object) {
        return replaceOrInsert(current -> Objects.equals(object, current), object);
    }

    @Override
    public boolean replaceOrInsert(@NotNull Predicate<T> predicate, @NotNull T object) {
        if (replace(predicate, object)==0) {
            return add(object);
        }
        return true;
    }

    /**
     * Invokes a method from the list and returns the type of value given.
     *
     * @param method to execute.
     * @param <V>    the type of the return value from the given method.
     * @return the return type of the method given.
     */
    protected <V> V parseValue(@NotNull Supplier<V> method, @NotNull ListAction.ListExecutionType type) {
        List<T> copy = List.copyOf(this);
        V executed = method.get();
        if (getListAction().isPresent()) {
            try {
                getListAction().get().execute(type, copy, List.copyOf(this));
            } catch (Throwable throwable) { // roll back in case of exception and pass exception on
                super.clear();
                super.addAll(copy);
                throw throwable;
            }
        }
        return executed;
    }

    @Override
    public void clearAndAddAll(@NotNull Collection<T> newList) {
        super.clear();
        addAll(newList);
    }
}
