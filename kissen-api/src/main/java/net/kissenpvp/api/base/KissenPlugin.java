package net.kissenpvp.api.base;

import org.jspecify.annotations.NonNull;

import java.io.File;

public interface KissenPlugin
{
    @NonNull String getName();

    @NonNull File getDataFolder();

    boolean isEnabled();

}
