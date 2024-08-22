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

import lombok.AccessLevel;
import lombok.Getter;
import net.kissenpvp.core.api.command.AbstractArgumentParser;
import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.annotations.ArgumentName;
import net.kissenpvp.core.api.command.annotations.Optional;
import net.kissenpvp.core.api.command.annotations.IgnoreQuote;
import net.kissenpvp.core.api.command.exception.ArgumentParserAbsentException;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.command.InternalCommandImplementation;
import net.kissenpvp.core.command.parser.EnumParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * This class is responsible for evaluating and processing the parameters of a command method,
 * transforming them into {@link Argument} objects that can be used by the command execution logic.
 * <p>
 * {@link MethodEvaluator} analyses each parameter of a given method, taking into account various metadata such as
 * its type, whether it is an array, whether it has an {@link IgnoreQuote} annotation, and its default value(s) as specified
 * by the {@link Optional} annotation.
 * <p>
 * While processing each parameter, this class also ensures that array parameters follow the proper command syntax by being placed
 * last in the command.
 * <p>
 * If a parameter type has an associated {@link AbstractArgumentParser}, it's used to deserialize default values (if any) to the
 * appropriate type. If no such parser exists for a parameter's type, an {@link ArgumentParserAbsentException} is thrown.
 * <p>
 * The main public method provided by this class is {@link #evaluateMethod(Method)}, which processes a method and returns
 * a list of its parameters represented as {@link Argument} objects.
 */
public class MethodEvaluator<S extends ServerEntity> {

    @Getter(AccessLevel.PRIVATE) private final Supplier<Map<Class<?>, AbstractArgumentParser<?, S>>> argumentSupplier;

    public MethodEvaluator(@NotNull Supplier<Map<Class<?>, AbstractArgumentParser<?, S>>> argumentSupplier)
    {
        this.argumentSupplier = argumentSupplier;
    }

    public @NotNull @Unmodifiable List<Argument<?, S>> evaluateMethod(@NotNull Method method) {
        List<Argument<?, S>> argumentList = new ArrayList<>();

        for (Parameter parameter : method.getParameters()) {
            argumentList.addAll(processParameter(getArgumentSupplier().get(), method, parameter));
        }

        return argumentList;
    }

    /**
     * Retrieves the {@link InternalCommandImplementation} singleton instance that exists within the {@link KissenCore}.
     * <p>
     * This function provides a simple and direct way of accessing the {@link InternalCommandImplementation} instance.
     * It's like a convenience proxy that simplifies the retrieval of the singleton instance by avoiding the need
     * to always use the full {@code KissenCore.getInstance().getImplementation(KissenCommandImplementation.class)} expression.
     *
     * @return The {@link InternalCommandImplementation} singleton instance that exists within the {@link KissenCore}.
     */
    private @NotNull InternalCommandImplementation<?> getCommandImplementation() {
        return KissenCore.getInstance().getImplementation(InternalCommandImplementation.class);
    }

    private @Unmodifiable <T> @NotNull List<Argument<T, S>> processParameter(@NotNull Map<Class<?>, AbstractArgumentParser<?, S>> parserMap, @NotNull Method method, @NotNull Parameter parameter)
    {
        Class<T> parameterType = (Class<T>) parameter.getType();

        ArgumentType argumentType = parameterType.isArray() ? ArgumentType.ARRAY : parameterType.equals(java.util.Optional.class) ? ArgumentType.OPTIONAL : ArgumentType.NONE;
        validateArrayPlacement(method.getParameters(), parameterType.isArray(), parameter);

        Argument.ArgumentBuilder<T, S> builder = Argument.<T, S>builder()
                .name(parameter.getName())
                .type(parameterType)
                .argumentType(argumentType)
                .ignoreQuote(parameter.isAnnotationPresent(IgnoreQuote.class));

        switch (argumentType)
        {
            case ARRAY -> builder.type(parameterType = (Class<T>) parameterType.getComponentType());
            case OPTIONAL ->
            {
                String typeName = parameter.getParameterizedType().getTypeName();
                typeName = typeName.substring("java.util.Optional<".length(), typeName.length() - 1);
                try
                {
                    builder.type(parameterType = (Class<T>) Class.forName(typeName));
                    builder.isNullable(true);
                    builder.defaultValue((T) java.util.Optional.empty());
                }
                catch (ClassNotFoundException classNotFoundException)
                {
                    throw new RuntimeException(String.format("Class %s was not not found in the classpath. Ensure the class is available in your project's classpath.", typeName));
                }
            }
            default -> {} // empty
        }
        boolean isEnum = parameterType.isEnum();
        builder.isEnum(isEnum);
        AbstractArgumentParser<T, S> abstractArgumentParser = (AbstractArgumentParser<T, S>) (isEnum ? new EnumParser<>(parameterType) : parserMap.get(parameterType));
        if(abstractArgumentParser!= null)
        {
            builder.argumentParser(abstractArgumentParser);

            if(parameter.isAnnotationPresent(ArgumentName.class))
            {
                builder.argumentName(parameter.getAnnotation(ArgumentName.class).value());
            }
            else if (abstractArgumentParser.argumentName() != null)
            {
                builder.argumentName(abstractArgumentParser.argumentName());
            }
            else
            {
                builder.argumentName(parameterType.getSimpleName().toLowerCase().charAt(0) + parameterType.getSimpleName().substring(1));
            }
        }

        Optional optionalValueAnnotated = parameter.getDeclaredAnnotation(Optional.class);
        if (CommandPayload.class.isAssignableFrom(parameterType) || optionalValueAnnotated == null) {
            return List.of(builder.build());
        }

        return List.of(createOptional(parserMap, method, parameterType, argumentType, optionalValueAnnotated.value(), builder));
    }


    private <T> Argument<T, S> createOptional(@NotNull Map<Class<?>, AbstractArgumentParser<?, S>> parserMap, @NotNull Method method, @NotNull Class<T> type, ArgumentType isArray, @NotNull String[] def, @NotNull Argument.ArgumentBuilder<T, S> builder) {

        if (type.isPrimitive() && def.length == 0) {
            throw new IllegalArgumentException(String.format("Use wrappers instead of primitive types for nullability, %s.", method.getName()));
        }

        final AbstractArgumentParser<?, ?> parser = parserMap.get(type);
        if (parser == null) {
            throw new ArgumentParserAbsentException(type);
        }

        builder.isNullable(true);
        switch (isArray)
        {
            case ARRAY -> builder.defaultValue((T) createArray(type, parser, def));
            case NONE -> builder.defaultValue((T) getCommandImplementation().deserialize(def[0], parser));
        }

        return builder.build();
    }

    /**
     * Creates an array of objects by using an {@link AbstractArgumentParser} to deserialize a given array of strings into specific types.
     * <p>
     * This function is primarily used in the construction of nullable or optional {@link Argument}s that have an array of default values.
     * <p>
     * Function iterates over the given array of string defaults, using the provided {@link AbstractArgumentParser} to deserialize each string into an instance of the specified type.
     * An array of these instances is then created and returned.
     *
     * @param type    The class object of the type that each element in the returned array should be.
     * @param adapter The {@link AbstractArgumentParser} to be used for deserializing each string in the default values array.
     * @param def     The array of string default values to be deserialized into specific types.
     * @return An array of objects, where each object is an instance of the specified type.
     */
    private Object[] createArray(Class<?> type, AbstractArgumentParser<?, ?> adapter, String @NotNull [] def) {
        Object[] value = (Object[]) Array.newInstance(type, 0);
        for (String arg : def) {
            value = getCommandImplementation().add(value, getCommandImplementation().deserialize(arg, adapter));
        }

        return value;
    }

    /**
     * Validates the placement of an array parameter in the list of parameters of a method.
     * <p>
     * In accordance to the API design, it is required for any array parameters to be placed last in any method's parameter ordering.
     * <p>
     * This function validates the placement of the given parameter, checking whether it is the last element in the parameter list if it is an array.
     * If the array parameter is not in the right position, an exception of type {@link RuntimeException} is thrown.
     *
     * @param parameters The array of method's parameters.
     * @param isArray    A boolean indicating whether the parameter to validate is an array.
     * @param parameter  The parameter to validate.
     * @throws RuntimeException If the array parameter is not the last in the list.
     */
    private void validateArrayPlacement(@NotNull Parameter[] parameters, boolean isArray, @NotNull Parameter parameter) {
        if (isArray && parameter != parameters[parameters.length - 1]) {
            throw new RuntimeException("Please ensure that arrays are placed as the final argument, in accordance with the required syntax.", new IllegalStateException());
        }
    }
}
