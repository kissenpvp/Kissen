package net.kissenpvp.core.user.usersettings;

import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import net.kissenpvp.core.api.user.usersetttings.PlayerSetting;
import org.jetbrains.annotations.NotNull;

public record RegisteredPlayerSetting(@NotNull KissenPlugin plugin, @NotNull PlayerSetting<?> playerSetting) { }
