package net.kissenpvp.core.permission;

import net.kissenpvp.core.api.permission.Permission;
import net.kissenpvp.core.api.permission.PermissionEntry;

public class TestPermission extends KissenPermission
{
    public TestPermission(PermissionNode permissionNode, PermissionEntry<? extends Permission> permissionEntry)
    {
        super(permissionNode, permissionEntry, (record) -> {}); // no data persistence
    }
}
