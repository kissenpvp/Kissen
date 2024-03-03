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
import net.kissenpvp.core.api.database.savable.list.KissenList;
import net.kissenpvp.core.api.database.savable.list.ListAction;
import net.kissenpvp.core.api.database.savable.list.ListExecution;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KissenKissenList<T> extends ArrayList<T> implements KissenList<T> {
    private ListAction listAction;

    public KissenKissenList(int initialCapacity) {
        super(initialCapacity);
    }

    public KissenKissenList(@NotNull Collection<? extends T> c) {
        super(c);
    }

    public KissenKissenList(@NotNull Collection<? extends T> c, ListAction listAction) {
        super(c);
        this.listAction = listAction;
    }

    @Override
    public @NotNull Optional<@NotNull ListAction> getListAction() {
        return Optional.ofNullable(listAction);
    }

    @Override
    public T set(int index, T element) {
        return parseValue(() -> super.set(index, element), () -> getListAction().ifPresent(action -> action.execute(ListExecution.SET, index, element)));
    }

    @Override
    public boolean add(T t) {
        return parseValue(() -> super.add(t), () -> getListAction().ifPresent(action -> action.execute(ListExecution.ADD, t)));
    }

    @Override
    public void add(int index, T element) {
        parseValue(() -> {
            super.add(index, element);
            return false;
        }, () -> getListAction().ifPresent(action -> action.execute(ListExecution.ADD_INDEX, index, element)));
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        return parseValue(() -> super.addAll(c.stream().toList()), () -> getListAction().ifPresent(action -> action.execute(ListExecution.ADD_ALL, c)));
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        return parseValue(() -> super.addAll(index, c), () -> getListAction().ifPresent(action -> action.execute(ListExecution.ADD_ALL_INDEX_INCLUDED, index, c)));
    }

    @Override
    public T remove(int index) {
        return parseValue(() -> super.remove(index), () -> getListAction().ifPresent(action -> action.execute(ListExecution.REMOVE_INDEX, index)));
    }

    @Override
    public boolean remove(Object o) {
        return parseValue(() -> super.remove(o), () -> getListAction().ifPresent(action -> action.execute(ListExecution.REMOVE, o)));
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        parseValue(() -> {
            super.removeRange(fromIndex, toIndex);
            return false;
        }, () -> listAction.execute(ListExecution.REMOVE_RANGE, fromIndex, toIndex));
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return parseValue(() -> super.removeAll(c), () -> getListAction().ifPresent(action -> action.execute(ListExecution.REMOVE_ALL, c)));
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        return parseValue(() -> super.removeIf(filter), () -> getListAction().ifPresent(action -> action.execute(ListExecution.REMOVE_IF, filter)));
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return parseValue(() -> super.retainAll(c), () -> getListAction().ifPresent(action -> action.execute(ListExecution.RETAIN_ALL, c)));
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator) {
        parseValue(() -> {
            super.replaceAll(operator);
            return true;
        }, () -> getListAction().ifPresent(action -> action.execute(ListExecution.REPLACE_ALL, operator)));
    }

    @Override
    public void clear() {
        parseValue(() -> {
            super.clear();
            return true;
        }, () -> getListAction().ifPresent(action -> action.execute(ListExecution.CLEAR)));
    }

    @Override
    public boolean[] invert(@NotNull T value) {
        return !contains(value) ? new boolean[]{true, add(value)} : new boolean[]{false, remove(value)};
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
        }, () -> getListAction().ifPresent(action -> action.execute(ListExecution.REPLACE, predicate, object)));
    }

    @Override
    public boolean replaceOrInsert(@NotNull T object) {
        return replaceOrInsert(current -> Objects.equals(object, current), object);
    }

    @Override
    public boolean replaceOrInsert(@NotNull Predicate<T> predicate, @NotNull T object) {
        if(replace(predicate, object) == 0)
        {
            return add(object);
        }
        return true;
    }

    /**
     * Invokes a method from the list and returns the type of value given.
     *
     * @param method   to execute.
     * @param runnable to execute when running this.
     * @param <V>      the type of the return value from the given method.
     * @return the return type of the method given.
     */
    protected <V> V parseValue(@NotNull Execute<V> method, Runnable runnable) {
        List<?> copy = List.copyOf(this);
        V executed = method.execute();
        if(!copy.equals(this))
        {
            getListAction().ifPresent(action -> runnable.run());
        }
        return executed;
    }

    @Override
    public void clearAndAddAll(@NotNull Collection<T> newList) {
        super.clear();
        addAll(newList);
    }

    /**
     * An {@link Serializable} interface which is there to parse the right return value, when executing something.
     *
     * @param <T> the type of the return value.
     */
    protected interface Execute<T> extends Serializable {
        T execute();
    }
}
