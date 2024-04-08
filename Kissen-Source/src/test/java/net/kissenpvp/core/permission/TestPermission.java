package net.kissenpvp.core.permission;

import net.kissenpvp.core.api.permission.AbstractPermission;
import net.kissenpvp.core.api.permission.AbstractPermissionEntry;

public class TestPermission extends KissenPermission
{
    public TestPermission(PermissionNode permissionNode, AbstractPermissionEntry<? extends AbstractPermission> permissionEntry)
    {
        super(permissionNode, permissionEntry, (record) -> {}); // no data persistence
    }
}
