package net.kissenpvp.api.database;

import org.jetbrains.annotations.NotNull;

public interface PersistableEntity<P>
{
    @NotNull P id();

    int signature();

    boolean unsaved();
}
