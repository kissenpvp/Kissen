package net.kissenpvp.core.permission;

import net.kissenpvp.core.api.permission.Permission;
import net.kissenpvp.core.api.permission.PermissionEntry;

public class TestPermission extends KissenPermission
{
    public TestPermission(KissenPermissionNode kissenPermissionNode, PermissionEntry<? extends Permission> permissionEntry)
    {
        super(kissenPermissionNode, permissionEntry, (record) -> {}); // no data persistence
    }
}
