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

package net.kissenpvp.core.command;

import net.kissenpvp.core.api.base.ExceptionHandler;
import net.kissenpvp.core.api.command.ArgumentParser;
import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.exception.deserialization.TemporaryDeserializationException;
import net.kissenpvp.core.api.command.handler.DateTimeParseExceptionHandler;
import net.kissenpvp.core.api.time.AccurateDuration;
import net.kissenpvp.core.command.handler.*;
import net.kissenpvp.core.command.parser.BooleanParser;
import net.kissenpvp.core.command.parser.ByteParser;
import net.kissenpvp.core.command.parser.CharacterParser;
import net.kissenpvp.core.command.parser.DoubleParser;
import net.kissenpvp.core.command.parser.AccurateDurationParser;
import net.kissenpvp.core.command.parser.FloatParser;
import net.kissenpvp.core.command.parser.IntegerParser;
import net.kissenpvp.core.command.parser.LongParser;
import net.kissenpvp.core.command.parser.NamedTextColorParser;
import net.kissenpvp.core.command.parser.ShortParser;
import net.kissenpvp.core.command.parser.StringParser;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.base.KissenImplementation;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.api.command.handler.BackendExceptionHandler;
import net.kissenpvp.core.message.localization.KissenLocalizationImplementation;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class KissenCommandImplementation implements KissenImplementation {

    private final Map<Class<?>, ArgumentParser<?, ?>> parserList;
    private final CommandExceptionHandlerService commandExceptionHandlerService;

    /**
     * Creates a new instance of KissenCommandImplementation.
     * Initializes the parserList with an empty HashMap.
     * Initializes the exceptionHandlers with an empty HashSet.
     */
    public KissenCommandImplementation() {
        this.parserList = new HashMap<>();
        this.commandExceptionHandlerService = new CommandExceptionHandlerService();

        // default
        registerParser(String.class, new StringParser<>());
        registerParser(Byte.class, new ByteParser<>());
        registerParser(Short.class, new ShortParser<>());
        registerParser(Integer.class, new IntegerParser<>());
        registerParser(Float.class, new FloatParser<>());
        registerParser(Double.class, new DoubleParser<>());
        registerParser(Long.class, new LongParser<>());
        registerParser(Character.class, new CharacterParser<>());
        registerParser(Boolean.class, new BooleanParser<>());

        // we love java, right?
        registerParser(Byte.TYPE, new ByteParser<>());
        registerParser(Short.TYPE, new ShortParser<>());
        registerParser(Integer.TYPE, new IntegerParser<>());
        registerParser(Float.TYPE, new FloatParser<>());
        registerParser(Double.TYPE, new DoubleParser<>());
        registerParser(Long.TYPE, new LongParser<>());
        registerParser(Character.TYPE, new CharacterParser<>());
        registerParser(Boolean.TYPE, new BooleanParser<>());

        // advanced
        registerParser(NamedTextColor.class, new NamedTextColorParser<>());
        registerParser(AccurateDuration.class, new AccurateDurationParser<>());

        // Handler
        registerHandler(new BackendExceptionHandler<>());
        registerHandler(new NumberFormatExceptionHandler<>());
        registerHandler(new CommandExceptionHandler<>());
        registerHandler(new InvocationTargetExceptionHandler<>());
        registerHandler(new DateTimeParseExceptionHandler<>());
        registerHandler(new NullPointerExceptionHandler<>());
        registerHandler(new InvalidColorExceptionHandler<>());
    }

    @Override
    public boolean start() {
        KissenLocalizationImplementation kissenLocalizationImplementation = KissenCore.getInstance().getImplementation(KissenLocalizationImplementation.class);
        kissenLocalizationImplementation.register("server.command.incorrect-usage", new MessageFormat("The command usage is not correct. Did you mean: {0}."));
        kissenLocalizationImplementation.register("server.command.invalid-duration", new MessageFormat("This duration does not seem to match the ISO 8601 duration format. If you want to know more: https://en.wikipedia.org/wiki/ISO_8601"));
        kissenLocalizationImplementation.register("server.command.backend-exception", new MessageFormat("It seems that the backend is currently unreachable. Please contact an Administrator."));
        kissenLocalizationImplementation.register("server.command.invalid.color", new MessageFormat("The color {0} does not exist."));
        return KissenImplementation.super.start();
    }

    /**
     * Registers a parser for a given type.
     * If the type already exists in the system, an exception will be thrown.
     *
     * @param <T>     the type to register parser for
     * @param <S>     the server entity type
     * @param type    the type to register parser for
     * @param parser  the parser to register
     * @throws IllegalArgumentException if the type already exists in the system
     */
    public <T, S extends ServerEntity> void registerParser(@NotNull Class<T> type, @NotNull ArgumentParser<T, S> parser) {
        if (!parserList.containsKey(type)) {
            parserList.put(type, parser);
            return;
        }
        throw new IllegalArgumentException(String.format("Type %s already exists in the system.", type.getSimpleName()));
    }

    /**
     * Registers an ExceptionHandler to be used by KissenCommandImplementation.
     *
     * @param exceptionHandler The ExceptionHandler to be registered.
     */
    public void registerHandler(@NotNull ExceptionHandler<?> exceptionHandler)
    {
        commandExceptionHandlerService.registerHandler(exceptionHandler);
    }

    /**
     * Handles a Throwable using the registered ExceptionHandlers.
     *
     * @param throwable The Throwable to be handled.
     */
    public <S extends ServerEntity> boolean handle(@NotNull CommandPayload<S> commandPayload, @NotNull Throwable throwable) {
        return commandExceptionHandlerService.handleThrowable(commandPayload, throwable);
    }


    public @NotNull @Unmodifiable Map<Class<?>, ArgumentParser<?, ?>> getParserList() {
        return Collections.unmodifiableMap(parserList);
    }

    /**
     * This method is used to deserialize a string using an ArgumentParser.
     *
     * @param input          the input string to be deserialized
     * @param argumentParser the parser to use for deserialization
     * @return the object deserialized from the input
     * @throws TemporaryDeserializationException if any exception occurs during deserialization
     */
    public @NotNull Object deserialize(@NotNull String input, @NotNull ArgumentParser<?, ?> argumentParser) {
        try {
            return argumentParser.deserialize(input);
        } catch (Exception exception) {
            argumentParser.processError(input, exception);
            throw new TemporaryDeserializationException(exception);
        }
    }

    /**
     * This method is used to add an element to an array.
     *
     * @param array   the array to which the element will be added
     * @param element the element to be added
     * @return a new array with the element added
     */
    public @NotNull Object[] add(@NotNull Object[] array, @NotNull Object element) {
        Class<?> type = determineType(array, element);
        return addElementToArray(array, element, type);
    }

    /**
     * Determines the type of array and an element.
     *
     * @param array   The input array.
     * @param element The input element.
     * @return The determined type of the array and element.
     * @throws NullPointerException if either the array or the element is null.
     */
    private Class<?> determineType(@NotNull Object[] array, @NotNull Object element) {
        return Objects.requireNonNullElse(array, element).getClass();
    }

    /**
     * Adds an element to an array and returns the updated array.
     *
     * @param array   the array to which the element is added
     * @param element the element to be added to the array
     * @param type    the class type of the array elements
     * @return the updated array with the element added
     */
    private @NotNull Object @NotNull [] addElementToArray(@NotNull Object[] array, @NotNull Object element, @NotNull Class<?> type) {
        Object[] newArray = copy(array, type);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * Creates a deep copy of the given array.
     *
     * @param array                   The array to be copied
     * @param newArrayComponentType   The class type of the new array
     * @return A deep copy of the given array
     */
    public @NotNull Object[] copy(@NotNull Object[] array, @NotNull Class<?> newArrayComponentType) {
        return (array != null) ? expandArray(array) : createSingleElementArray(newArrayComponentType);
    }

    /**
     * Expands the given array by one element, creating a new array with the same type and
     * the length increased by one. The element at the last index is set to `null`.
     *
     * @param array the array to be expanded (must not be null)
     * @return the expanded array
     */
    private @NotNull Object[] expandArray(@NotNull Object[] array) {
        int arrayLength = Array.getLength(array);
        Object[] expandedArray = (Object[]) Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);

        System.arraycopy(array, 0, expandedArray, 0, arrayLength);

        return expandedArray;
    }

    /**
     * Creates a single element array of the specified type.
     *
     * @param type the class object representing the type of the array element
     * @return an array with a single element of the specified type
     */
    private @NotNull Object[] createSingleElementArray(@NotNull Class<?> type) {
        return (Object[]) Array.newInstance(type, 1);
    }
}
