package net.kissenpvp.core.command.parser;

import lombok.AccessLevel;
import lombok.Getter;
import net.kissenpvp.core.api.command.ArgumentParser;
import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class EnumParser<T extends Enum<T>, S extends ServerEntity> implements ArgumentParser<T, S> {

    @Getter(AccessLevel.PRIVATE) private final Class<?> enumInstance;

    public EnumParser(Class<?> enumInstance) {
        this.enumInstance = enumInstance;
    }

    @Override
    public @NotNull String serialize(@NotNull T object) {
        return object.name();
    }

    @Override
    public @NotNull T deserialize(@NotNull String input) {
        try
        {
            return Enum.valueOf((Class<T>) getEnumInstance(), input.toUpperCase());
        }catch (IllegalArgumentException illegalArgumentException)
        {
            throw new EnumConstantNotPresentException((Class<? extends Enum<?>>) enumInstance, input);
        }
    }

    @Override
    public @NotNull Collection<String> tabCompletion(@NotNull CommandPayload<S> commandPayload) {
        return Arrays.stream(((Class<T>) enumInstance).getEnumConstants()).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());
    }
}
