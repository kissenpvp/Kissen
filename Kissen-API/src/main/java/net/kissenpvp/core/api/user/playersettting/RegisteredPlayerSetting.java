package net.kissenpvp.core.api.user.playersettting;

import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface RegisteredPlayerSetting<T, S extends PlayerClient<?, ?, ?>> {

    @NotNull
    AbstractPlayerSetting<T, S> getParent();

    @NotNull
    Class<? extends AbstractPlayerSetting<T, S>> getParentClass();

    @NotNull
    KissenPlugin getPlugin();

    @NotNull
    Optional<String> getPermission();

}
