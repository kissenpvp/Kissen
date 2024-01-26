package net.kissenpvp.core.api.base.plugin;

import net.kissenpvp.core.api.command.ArgumentParser;
import org.jetbrains.annotations.NotNull;

public interface KissenCommandManager
{

    void registerCommand(@NotNull Object... commands);

    <T> void registerParser(@NotNull Class<T> type, @NotNull ArgumentParser<T, ?> parser);

}
