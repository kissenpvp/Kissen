package net.kissenpvp.core.permission;

import net.kissenpvp.core.api.base.Implementation;
import net.kissenpvp.core.api.database.connection.DatabaseImplementation;
import net.kissenpvp.core.api.database.meta.Meta;
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.permission.AbstractPermission;
import net.kissenpvp.core.api.permission.AbstractPermissionGroup;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.KissenTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;

public abstract class InternalKissenPermissionImplementation<T extends AbstractPermission> implements Implementation {

    private KissenTable publicTable;

    @Override
    public boolean preStart() {
        DatabaseImplementation implementation = KissenCore.getInstance().getImplementation(DatabaseImplementation.class);
        publicTable = (KissenTable) implementation.getPrimaryConnection().createTable("kissen_permission_group_table");
        return Implementation.super.preStart();
    }

    public @NotNull KissenTable getTable() {
        return publicTable;
    }

    public @NotNull Meta getMeta() {
        return getTable().setupMeta(null);
    }

    public abstract void addPermission(@NotNull String permission);

    @NotNull
    public abstract AbstractPermissionGroup<?> create(@NotNull String name) throws EventCancelledException;

    @NotNull
    public abstract Optional<AbstractPermissionGroup<?>> getPermissionGroupSavable(@NotNull String name);

    @NotNull
    @Unmodifiable
    public abstract Set<AbstractPermissionGroup<T>> getInternalGroups();

    public abstract void removePermissionGroup(@NotNull String name);

}
