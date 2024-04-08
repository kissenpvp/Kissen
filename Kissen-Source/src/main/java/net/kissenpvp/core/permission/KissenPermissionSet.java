package net.kissenpvp.core.permission;

import net.kissenpvp.core.api.permission.AbstractPermission;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class KissenPermissionSet<T extends AbstractPermission> extends HashSet<T>
{

    public KissenPermissionSet()
    {
        super();
    }

    public KissenPermissionSet(@NotNull Collection<? extends T> c)
    {
        super(c);
    }

    public KissenPermissionSet(int initialCapacity, float loadFactor)
    {
        super(initialCapacity, loadFactor);
    }

    public KissenPermissionSet(int initialCapacity)
    {
        super(initialCapacity);
    }

    @Override
    public boolean add(T t)
    {
        if(containPredicate().test(t))
        {
            return super.add(t);
        }
        return false;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c)
    {
        return super.addAll(c.stream().filter(containPredicate()).collect(Collectors.toSet()));
    }

    private @NotNull Predicate<T> containPredicate()
    {
        return current -> stream().noneMatch(eq -> Objects.equals(current.getName(), eq.getName()));
    }
}
