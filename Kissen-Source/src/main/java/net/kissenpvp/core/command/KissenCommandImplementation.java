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
import net.kissenpvp.core.api.base.Implementation;
import net.kissenpvp.core.api.command.ArgumentParser;
import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.exception.deserialization.TemporaryDeserializationException;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class KissenCommandImplementation implements Implementation {

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
    }

    public <T, S extends ServerEntity> void registerParser(@NotNull Class<T> type, @NotNull ArgumentParser<T, S> parser) {
        if (!parserList.containsKey(type)) {
            parserList.put(type, parser);
            return;
        }
        throw new IllegalArgumentException(String.format("Type %s already exists in the system. Unable to proceed with the operation. Please use a different type or ensure the existing type is correctly removed before attempting again.", type.getSimpleName()));
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

    public @NotNull Object[] add(@NotNull Object[] array, @NotNull Object element) {
        Class<?> type = determineType(array, element);
        return addElementToArray(array, element, type);
    }

    private Class<?> determineType(@NotNull Object[] array, @NotNull Object element) {
        return Objects.requireNonNullElse(array, element).getClass();
    }

    private @NotNull Object[] addElementToArray(@NotNull Object[] array, @NotNull Object element, @NotNull Class<?> type) {
        Object[] newArray = copy(array, type);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public @NotNull Object[] copy(@NotNull Object[] array, @NotNull Class<?> newArrayComponentType) {
        return (array != null) ? expandArray(array) : createSingleElementArray(newArrayComponentType);
    }

    private @NotNull Object[] expandArray(@NotNull Object[] array) {
        int arrayLength = Array.getLength(array);
        Object[] expandedArray = (Object[]) Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);

        System.arraycopy(array, 0, expandedArray, 0, arrayLength);

        return expandedArray;
    }

    private @NotNull Object[] createSingleElementArray(@NotNull Class<?> type) {
        return (Object[]) Array.newInstance(type, 1);
    }
}
