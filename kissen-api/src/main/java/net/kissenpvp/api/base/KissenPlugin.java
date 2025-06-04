package net.kissenpvp.api.base;

import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface KissenPlugin
{

    @Subst("plugin_identifier") @NotNull String getName();

    @NotNull File getDataFolder();

    boolean isEnabled();

}
