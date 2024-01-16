package net.kissenpvp.core.message.usersettings;

import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.user.usersetttings.PlayerSetting;
import net.kissenpvp.core.api.user.usersetttings.UserValue;
import org.jetbrains.annotations.NotNull;

public class HighlightVariables implements PlayerSetting<Boolean>
{
    @Override
    public @NotNull String serialize(@NotNull Boolean object)
    {
        return object.toString();
    }

    @Override
    public @NotNull Boolean deserialize(@NotNull String input)
    {
        return Boolean.parseBoolean(input);
    }

    @Override
    public @NotNull String getKey()
    {
        return "highlight";
    }

    @Override
    public @NotNull Boolean getDefaultValue(@NotNull PlayerClient<?, ?, ?> playerClient)
    {
        return true;
    }

    @Override
    public @NotNull UserValue<Boolean>[] getPossibleValues(@NotNull PlayerClient<?, ?, ?> playerClient)
    {
        return new UserValue[] {new UserValue<>(true), new UserValue<>(false)};
    }
}
