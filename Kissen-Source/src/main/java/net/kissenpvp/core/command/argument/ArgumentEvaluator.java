/*
 * Copyright (C) 2023 KissenPvP
 *
 * This program is licensed under the Apache License, Version 2.0.
 *
 * This software may be redistributed and/or modified under the terms
 * of the Apache License as published by the Apache Software Foundation,
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the Apache
 * License, Version 2.0 for the specific language governing permissions
 * and limitations under the License.
 *
 * You should have received a copy of the Apache License, Version 2.0
 * along with this program. If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package net.kissenpvp.core.command.argument;

import net.kissenpvp.core.api.command.ArgumentParser;
import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.exception.ArgumentMissingException;
import net.kissenpvp.core.api.command.exception.deserialization.DeserializationException;
import net.kissenpvp.core.api.command.exception.deserialization.TemporaryDeserializationException;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.command.CommandImplementation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@code ArgumentEvaluator} class is responsible for parsing command arguments
 * according to predefined rules and argument handler. It works in conjunction with
 * the {@code Argument} and {@code CommandPayload} to achieve this.
 *
 * @see Argument
 * @see CommandPayload
 */
public record ArgumentEvaluator<S extends ServerEntity>(@NotNull @Unmodifiable List<Argument<?, S>> arguments) {

    /**
     * This method is used to parse command arguments.
     * It translates the command payload into an array of objects that correspond
     * to the list of arguments provided to this {@link ArgumentEvaluator}.
     *
     * @param commandPayload the input to be parsed
     * @return array of parsed objects
     * @throws TemporaryDeserializationException is thrown when an error occurs during parsing.
     */
    public @NotNull Object[] parseArguments(@NotNull CommandPayload<S> commandPayload) throws DeserializationException {

        Object[] parameters = new Object[0];
        AtomicInteger currentArgumentIndex = new AtomicInteger(0);
        CommandImplementation commandImplementation = KissenCore.getInstance().getImplementation(CommandImplementation.class);

        for (Argument<?, S> argument : arguments) {
            if (CommandPayload.class.isAssignableFrom(argument.type())) {
                parameters = commandImplementation.add(parameters, commandPayload);
                continue;
            }

            Optional<String> argumentOptional = readFullString(argument, currentArgumentIndex, commandPayload);

            if (argumentOptional.isEmpty()) {
                parameters = handleEmptyArgument(parameters, argument, currentArgumentIndex);
                continue;
            }

            Object object = switch (argument.argumentType())
            {
                case ARRAY -> processArrayArgument(argumentOptional.get(), argument, commandPayload, currentArgumentIndex);
                case OPTIONAL -> Optional.of(deserialize(argumentOptional.get(), argument.argumentParser()));
                case NONE -> deserialize(argumentOptional.get(), argument.argumentParser());
            };

            parameters = commandImplementation.add(parameters, object);
        }

        return parameters;
    }

    /**
     * This private method is an internal helper used by the parseArguments function.
     * It expresses the behavior when an argumentOptional is empty.
     *
     * @param parameters           the current parameters
     * @param argument             the argument
     * @param currentArgumentIndex the index tracked of the current argument
     * @return a new array that includes the parameter
     * @throws NullPointerException if the argument is not nullable
     */
    private @NotNull Object[] handleEmptyArgument(@NotNull Object[] parameters, @NotNull Argument<?, S> argument, @NotNull AtomicInteger currentArgumentIndex) throws NullPointerException {
        if (!argument.isNullable()) {
            throw new ArgumentMissingException(String.format("The argument '%s' cannot be null or undefined.", argument.type().getName()));
        }

        currentArgumentIndex.incrementAndGet();
        return KissenCore.getInstance()
                .getImplementation(CommandImplementation.class)
                .add(parameters, argument.defaultValue());
    }

    /**
     * Helper method that processes an array argument.
     *
     * @param argument             the argument specification
     * @param commandPayload       the command payload
     * @param currentArgumentIndex the current argument index
     * @return parsed array object
     */
    private @NotNull Object processArrayArgument(@NotNull String argumentValue, @NotNull Argument<?, S> argument, @NotNull CommandPayload<S> commandPayload, @NotNull AtomicInteger currentArgumentIndex) {
        Object object = Array.newInstance(argument.type(), 0);

        do {
            object = KissenCore.getInstance()
                    .getImplementation(CommandImplementation.class)
                    .add(
                            (Object[]) object,
                            deserialize(argumentValue, argument.argumentParser())
                    );
        } while ((argumentValue = readFullString(argument, currentArgumentIndex, commandPayload).orElse(null)) != null);

        return object;
    }

    /**
     * This method is used to deserialize a string using an ArgumentParser.
     *
     * @param input          the input string to be deserialized
     * @param argumentParser the parser to use for deserialization
     * @return the object deserialized from the input
     * @throws TemporaryDeserializationException if any exception occurs during deserialization
     */
    private @NotNull Object deserialize(@NotNull String input, @NotNull ArgumentParser<?, ?> argumentParser) {
        try {
            return argumentParser.deserialize(input);
        } catch (Exception exception) {
            argumentParser.processError(input, exception);
            if(exception instanceof DeserializationException deserializationException)
            {
                throw deserializationException;
            }
            throw new DeserializationException(exception);
        }
    }

    /**
     * Helper method for parsing argument string values.
     *
     * @param argument        the argument specification
     * @param currentArgument the current argument value tracked
     * @param commandPayload  the command payload
     * @return the parsed argument string encapsulated in an Optional
     */
    private @NotNull Optional<String> readFullString(@NotNull Argument<?, S> argument, @NotNull AtomicInteger currentArgument, @NotNull CommandPayload<S> commandPayload) {
        return commandPayload.getArgument(currentArgument.get()).map(argumentValue -> {
            currentArgument.incrementAndGet();
            if (!argument.ignoreQuote() && argumentValue.charAt(0) == '"') {
                final StringBuilder builder = new StringBuilder(argumentValue.substring(1));
                Optional<String> nextArgument;
                while ((nextArgument = commandPayload.getArgument(currentArgument.get())).isPresent()) {
                    currentArgument.incrementAndGet();
                    if (readArgumentString(argumentValue, builder, nextArgument.get())) {
                        break;
                    }
                }

                return builder.toString().replace("\\\"", "\"");
            }
            return argumentValue;
        });
    }

    /**
     * Processes an argument value for quotable parameters and builds up the argument's string value.
     *
     * @param argumentValue the current argument value
     * @param builder       the StringBuilder for building the argument
     * @param nextArgument  the next argument value
     * @return true if the next argument value ends with an unescaped quote, false otherwise
     */
    private boolean readArgumentString(@NotNull String argumentValue, @NotNull StringBuilder builder, @NotNull String nextArgument) {
        builder.append(" ");
        final int length = nextArgument.length();
        if (nextArgument.charAt(length - 1) == '"' && (length == 1 || nextArgument.charAt(length - 2) != '\\')) {
            builder.append(nextArgument, 0, length - 1);
            return true;
        }
        builder.append(argumentValue);
        return false;
    }

    /**
     * This method builds a user-friendly usage string for commands based on the given command name
     * and the argument list.
     *
     * @param name the command name
     * @return a string representation of how to properly use the command
     */
    public @NotNull String buildUsage(@NotNull String name) {
        final StringBuilder builder = new StringBuilder(name);
        for (Argument<?, S> argument : arguments) {
            if (CommandPayload.class.isAssignableFrom(argument.type())) continue;

            builder.append(" ").append(argument.isNullable() ? "[" : "").append("<");
            builder.append(argument.argumentName());

            if (argument.argumentType().equals(ArgumentType.ARRAY)) builder.append("...");

            builder.append(">").append(argument.isNullable() ? "]" : "");
        }

        return builder.toString();
    }
}
