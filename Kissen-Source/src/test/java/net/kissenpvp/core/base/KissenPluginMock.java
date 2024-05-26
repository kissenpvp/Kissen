package net.kissenpvp.core.base;

import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class KissenPluginMock implements KissenPlugin {
    @Override
    public @NotNull String getName() {
        return "test-plugin";
    }

    @Override
    public @NotNull File getDataFolder() {
        return new File("");
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
