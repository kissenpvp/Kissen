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

import lombok.Getter;
import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import net.kissenpvp.core.api.command.ArgumentParser;
import net.kissenpvp.core.api.command.CommandHandler;
import net.kissenpvp.core.api.command.exception.deserialization.TemporaryDeserializationException;
import net.kissenpvp.core.api.command.handler.BackendExceptionHandler;
import net.kissenpvp.core.api.command.handler.DateTimeParseExceptionHandler;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.api.time.AccurateDuration;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.base.KissenImplementation;
import net.kissenpvp.core.command.exceptionhandler.*;
import net.kissenpvp.core.command.handler.InternalCommandHandler;
import net.kissenpvp.core.command.handler.PluginCommandHandler;
import net.kissenpvp.core.command.parser.*;
import net.kissenpvp.core.message.localization.KissenLocalizationImplementation;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.*;

@Getter
public abstract class CommandImplementation<S extends ServerEntity> implements KissenImplementation
{
    private final Set<PluginCommandHandler<S, ?>> handler;
    private final InternalCommandHandler<S, ?> internalHandler;
    private final TargetValidator targetValidator;

    /**
     * Creates a new instance of KissenCommandImplementation.
     * Initializes the parserList with an empty HashMap.
     * Initializes the exceptionHandlers with an empty HashSet.
     */
    public CommandImplementation() {
        this.handler = new HashSet<>();
        this.internalHandler = constructInternalHandler();
        this.targetValidator = new TargetValidator();

        // default
        getInternalHandler().registerParser(String.class, new StringParser<>());
        getInternalHandler().registerParser(Byte.class, new ByteParser<>());
        getInternalHandler().registerParser(Short.class, new ShortParser<>());
        getInternalHandler().registerParser(Integer.class, new IntegerParser<>());
        getInternalHandler().registerParser(Float.class, new FloatParser<>());
        getInternalHandler().registerParser(Double.class, new DoubleParser<>());
        getInternalHandler().registerParser(Long.class, new LongParser<>());
        getInternalHandler().registerParser(Character.class, new CharacterParser<>());
        getInternalHandler().registerParser(Boolean.class, new BooleanParser<>());

        // we love java, right?
        getInternalHandler().registerParser(Byte.TYPE, new ByteParser<>());
        getInternalHandler().registerParser(Short.TYPE, new ShortParser<>());
        getInternalHandler().registerParser(Integer.TYPE, new IntegerParser<>());
        getInternalHandler().registerParser(Float.TYPE, new FloatParser<>());
        getInternalHandler().registerParser(Double.TYPE, new DoubleParser<>());
        getInternalHandler().registerParser(Long.TYPE, new LongParser<>());
        getInternalHandler().registerParser(Character.TYPE, new CharacterParser<>());
        getInternalHandler().registerParser(Boolean.TYPE, new BooleanParser<>());

        // advanced
        getInternalHandler().registerParser(NamedTextColor.class, new NamedTextColorParser<>());
        getInternalHandler().registerParser(AccurateDuration.class, new AccurateDurationParser<>());

        // Handler
        getInternalHandler().registerExceptionHandler(new BackendExceptionHandler<>());
        getInternalHandler().registerExceptionHandler(new NumberFormatExceptionHandler<>());
        getInternalHandler().registerExceptionHandler(new CommandExceptionHandler<>());
        getInternalHandler().registerExceptionHandler(new InvocationTargetExceptionHandler<>());
        getInternalHandler().registerExceptionHandler(new DateTimeParseExceptionHandler<>());
        getInternalHandler().registerExceptionHandler(new NullPointerExceptionHandler<>());
        getInternalHandler().registerExceptionHandler(new InvalidColorExceptionHandler<>());
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

    @Override
    public void load(@NotNull KissenPlugin kissenPlugin)
    {
        KissenCore.getInstance().getLogger().info("Register command handler for {}.", kissenPlugin.getName());
        registerHandler(kissenPlugin);
    }

    protected void registerHandler(@NotNull KissenPlugin kissenPlugin)
    {
        PluginCommandHandler<S, ?> pluginHandler = constructHandler(kissenPlugin);
        if (getHandler().contains(pluginHandler))
        {
            throw new IllegalArgumentException(String.format("Command handler for %s is already registered.", kissenPlugin.getName()));
        }
        this.handler.add(pluginHandler);
    }

    protected abstract InternalCommandHandler<S, ?> constructInternalHandler();

    protected abstract @NotNull PluginCommandHandler<S, ?> constructHandler(@NotNull KissenPlugin kissenPlugin);

    public @NotNull @Unmodifiable Set<PluginCommandHandler<S, ?>> getHandler()
    {
        return Collections.unmodifiableSet(handler);
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
    private @NotNull Class<?> determineType(@NotNull Object[] array, @NotNull Object element) {
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
