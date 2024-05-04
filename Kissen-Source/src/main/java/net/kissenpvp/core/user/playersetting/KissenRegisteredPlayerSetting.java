package net.kissenpvp.core.user.playersetting;

import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.user.playersettting.AbstractPlayerSetting;
import net.kissenpvp.core.api.user.playersettting.RegisteredPlayerSetting;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public record KissenRegisteredPlayerSetting<T, S extends PlayerClient<?, ?, ?>>(@NotNull KissenPlugin plugin,
                                                                                @NotNull AbstractPlayerSetting<T, S> setting) implements RegisteredPlayerSetting<T, S> {
    @Override
    public @NotNull AbstractPlayerSetting<T, S> getParent() {
        return setting();
    }

    @Override
    public @NotNull Class<? extends AbstractPlayerSetting<T, S>> getParentClass() {
        return (Class<? extends AbstractPlayerSetting<T, S>>) setting().getClass();
    }

    @Override
    public @NotNull KissenPlugin getPlugin() {
        return plugin();
    }

    @Override
    public @NotNull Optional<String> getPermission() {
        if (!setting().hasPermission()) {
            return Optional.empty();
        }

        return Optional.of(Objects.requireNonNullElseGet(setting().getPermission(), () -> {
            String template = "%s.setting.%s";
            return String.format(template, plugin().getName().toLowerCase(), setting().getKey().toLowerCase());
        }));
    }
}
