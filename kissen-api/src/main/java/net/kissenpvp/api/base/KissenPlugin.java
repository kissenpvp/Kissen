package net.kissenpvp.api.base;

import org.intellij.lang.annotations.Subst;
import org.jspecify.annotations.NonNull;

import java.io.File;

public interface KissenPlugin
{
    @Subst("plugin_identifier") @NonNull String getName();

    @NonNull File getDataFolder();

    boolean isEnabled();

}
