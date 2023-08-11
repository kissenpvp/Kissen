package net.kissenpvp.core.api.command;

import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public interface CommandPayload<S> {

    @NotNull String getLabel();

    @NotNull S getSender();

    @NotNull CommandTarget getTarget();

    @NotNull String[] getArguments();

    default int getArgumentCount() {
        return getArguments().length;
    }

    default @NotNull Optional<String> getArgument(int index) {
        try {
            return Optional.of(getArguments()[index]);
        } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            return Optional.empty();
        }
    }

    default String[] getArgs(int from, int to) throws ArrayIndexOutOfBoundsException {
        return Arrays.copyOfRange(getArguments(), from, to);
    }

    <T> @NotNull T[] getArgument(int from, int to, @NotNull Class<T> type) throws ArrayIndexOutOfBoundsException;

    boolean validate(@NotNull ServerEntity serverEntity);
}
