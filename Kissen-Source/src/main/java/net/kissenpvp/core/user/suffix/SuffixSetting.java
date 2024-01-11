package net.kissenpvp.core.user.suffix;

import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.time.TemporalObject;
import net.kissenpvp.core.api.user.suffix.Suffix;
import net.kissenpvp.core.api.user.usersetttings.PlayerSetting;
import net.kissenpvp.core.api.user.usersetttings.UserValue;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SuffixSetting implements PlayerSetting<String>
{

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

    @Override
    public @NotNull String getKey()
    {
        return "suffix";
    }

    @Override
    public @NotNull String getDefaultValue(@NotNull PlayerClient<?, ?, ?> playerClient)
    {
        return "none";
    }

    @Override
    public @NotNull UserValue<String>[] getPossibleValues(@NotNull PlayerClient<?, ?, ?> playerClient)
    {
        Stream<Suffix> suffixStream = playerClient.getSuffixSet().stream().filter(TemporalObject::isValid);
        Function<Suffix, UserValue<String>> transform = suffix -> new UserValue<>(suffix.getName());
        Set<UserValue<String>> settings = suffixStream.map(transform).collect(Collectors.toSet());
        settings.add(new UserValue<>("none"));
        return settings.toArray(UserValue[]::new);
    }
}
