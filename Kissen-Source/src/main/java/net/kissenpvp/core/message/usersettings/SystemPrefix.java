package net.kissenpvp.core.message.usersettings;

import net.kissenpvp.core.api.config.ConfigurationImplementation;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.user.usersetttings.PlayerSetting;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.message.settings.DefaultSystemPrefix;
import org.jetbrains.annotations.NotNull;

public class SystemPrefix implements PlayerSetting<String>
{
    @Override
    public @NotNull String getKey()
    {
        return "prefix";
    }

    @Override
    public @NotNull String getDefaultValue(@NotNull PlayerClient<?, ?, ?> playerClient)
    {
        return KissenCore.getInstance().getImplementation(ConfigurationImplementation.class).getSetting(
                DefaultSystemPrefix.class);
    }

    @Override
    public @NotNull String serialize(@NotNull String object)
    {
        return object;
    }

    @Override
    public @NotNull String deserialize(@NotNull String input)
    {
        return input;
    }
}
