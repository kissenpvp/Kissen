package net.kissenpvp.core.permission;

import net.kissenpvp.core.api.networking.socket.DataPackage;
import net.kissenpvp.core.api.permission.GroupablePermissionEntry;
import net.kissenpvp.core.database.DataWriter;
import net.kissenpvp.core.database.savable.SerializableSavableHandler;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

class TestPermissionEntry extends KissenPermissionEntry<TestPermission>
{

    @Override
    protected @org.jetbrains.annotations.NotNull TestPermission translatePermission(@org.jetbrains.annotations.NotNull PermissionNode permissionNode, @org.jetbrains.annotations.Nullable DataWriter dataWriter)
    {
        return new TestPermission(permissionNode, this);
    }

    @Override
    public @org.jetbrains.annotations.NotNull @org.jetbrains.annotations.Unmodifiable Set<UUID> getAffectedUsers()
    {
        return Collections.emptySet();
    }

    @Override
    public @org.jetbrains.annotations.NotNull @org.jetbrains.annotations.Unmodifiable Set<GroupablePermissionEntry<TestPermission>> getConnectedEntries()
    {
        return Collections.emptySet();
    }

    @Override
    public void permissionUpdate()
    {
        // Ignored
    }

    @Override
    public void sendData(@org.jetbrains.annotations.NotNull DataPackage dataPackage)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public SerializableSavableHandler getSerializableSavableHandler()
    {
        throw new UnsupportedOperationException();
    }
}
