package net.kissenpvp.core.command.argument;

import net.kissenpvp.core.api.command.ArgumentParser;
import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.annotations.Default;
import net.kissenpvp.core.api.command.annotations.IgnoreQuote;
import net.kissenpvp.core.api.command.exception.ArgumentParserAbsentException;
import net.kissenpvp.core.api.command.exception.CommandException;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.command.KissenCommandImplementation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class is responsible for evaluating and processing the parameters of a command method,
 * transforming them into {@link Argument} objects that can be used by the command execution logic.
 * <p>
 * {@link MethodEvaluator} analyses each parameter of a given method, taking into account various metadata such as
 * its type, whether it is an array, whether it has an {@link IgnoreQuote} annotation, and its default value(s) as specified
 * by the {@link Default} annotation.
 * <p>
 * While processing each parameter, this class also ensures that array parameters follow the proper command syntax by being placed
 * last in the command.
 * <p>
 * If a parameter type has an associated {@link ArgumentParser}, it's used to deserialize default values (if any) to the
 * appropriate type. If no such parser exists for a parameter's type, an {@link ArgumentParserAbsentException} is thrown.
 * <p>
 * The main public method provided by this class is {@link #evaluateMethod(Method)}, which processes a method and returns
 * a list of its parameters represented as {@link Argument} objects.
 */
public class MethodEvaluator<S> {

    /**
     * Evaluates a given method, analyzing each of its parameters to extract {@link Argument} objects from them.
     * <p>
     * This function iterates over each parameter in the provided method. Each parameter is then processed using
     * the {@link #processParameter(Method, Parameter)} function which generates a list of {@link Argument} objects
     * that represent the parameter.
     * <p>
     * All these lists are then combined into a single list, which is finally returned.
     * <p>
     * This function ensures that every single parameter of the provided method is represented as an {@link Argument}
     * in the returned list.
     *
     * @param method The method whose parameters are to be evaluated and represented as a list of {@link Argument} objects.
     * @return A list of {@link Argument} objects representing each parameter in the provided method.
     * @throws IllegalArgumentException      If a parameter in the provided method is an array but not placed as the last argument.
     * @throws ArgumentParserAbsentException If an ArgumentParser could not be retrieved for a parameter's type.
     */
    public @NotNull @Unmodifiable List<Argument<?, S>> evaluateMethod(@NotNull Method method) {
        List<Argument<?, S>> argumentList = new ArrayList<>();

        for (Parameter parameter : method.getParameters()) {
            argumentList.addAll(processParameter(method, parameter));
        }

        return argumentList;
    }

    /**
     * Retrieves the {@link KissenCommandImplementation} singleton instance that exists within the {@link KissenCore}.
     * <p>
     * This function provides a simple and direct way of accessing the {@link KissenCommandImplementation} instance.
     * It's like a convenience proxy that simplifies the retrieval of the singleton instance by avoiding the need
     * to always use the full {@code KissenCore.getInstance().getImplementation(KissenCommandImplementation.class)} expression.
     *
     * @return The {@link KissenCommandImplementation} singleton instance that exists within the {@link KissenCore}.
     */
    private @NotNull KissenCommandImplementation getCommandImplementation() {
        return KissenCore.getInstance().getImplementation(KissenCommandImplementation.class);
    }

    /**
     * Processes a single parameter of a provided method, transforming it into a list of one or more {@link Argument} objects.
     * <p>
     * This function first analyzes the provided parameter while validating its type and position in case it is an array.
     * This is done using the {@link #validateArrayPlacement(Parameter[], boolean, Parameter)} function.
     * <p>
     * It then builds an {@link Argument} using properties such as its name, type, array status, and any {@link IgnoreQuote} annotations present.
     * <p>
     * If the parameter's type is assignable from {@link CommandPayload}, or it does not have a {@link Default} annotation,
     * the function considers it to be a basic argument and returns it wrapped in a list.
     * <p>
     * However, if the parameter has a {@link Default} annotation, it means that it might have a default value(s) specified.
     * In this case, it will generate a "nullable" optional {@link Argument} using the {@link #createOptional(Method, Class, boolean, String[], Argument.ArgumentBuilder)} function.
     * <p>
     * In both cases, a list containing the single {@link Argument} that was processed from this parameter is returned.
     *
     * @param method    The method where the parameter originates from.
     * @param parameter The parameter to be processed into an {@link Argument}.
     * @return A list with one {@link Argument} representing the provided parameter.
     * @throws IllegalArgumentException      If the parameter is of a primitive type but does not have a default value, or if the parameter is an array but not placed as the last argument.
     * @throws ArgumentParserAbsentException If an ArgumentParser could not be retrieved for the parameter's type.
     */
    private <T> @NotNull List<Argument<T, S>> processParameter(@NotNull Method method, @NotNull Parameter parameter) {
        Class<T> parameterType = (Class<T>) parameter.getType();
        ArgumentType argumentType = parameterType.isArray() ? ArgumentType.ARRAY : parameterType.equals(Optional.class) ? ArgumentType.OPTIONAL : ArgumentType.NONE;

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
                    builder.defaultValue((T) Optional.empty());
                }
                catch (ClassNotFoundException classNotFoundException)
                {
                    throw new CommandException(String.format("Class %s was not not found.", typeName));
                }
            }
            default -> {} // empty
        }

        builder.argumentParser((ArgumentParser<T, S>) getCommandImplementation().getParserList().get(parameterType));

        Default defaultValueAnnotated = parameter.getDeclaredAnnotation(Default.class);
        if (CommandPayload.class.isAssignableFrom(parameterType) || defaultValueAnnotated == null) {
            return List.of(builder.build());
        }

        return List.of(createOptional(method, parameterType, argumentType, defaultValueAnnotated.value(), builder));
    }

    /**
     * Creates an optional {@link Argument} using the provided metadata.
     * <p>
     * This function is used to transform method parameters that have a {@link Default} annotation into nullable or optional {@link Argument}s.
     * An optional {@link Argument} is characterized by the fact that it can be left unspecified when invoking the command because it will have a default value.
     * <p>
     * Default values are specified as an array of strings in the {@link Default} annotation of the parameter. Each string will be deserialized into the required type {@code T}
     * to create the default value of the {@link Argument}.
     * <p>
     * If the type {@code T} of the {@link Argument} is an array, the default values will be an array as well.
     * <p>
     * Note: Primitive types are not supported for optional arguments because they can't be null. If a primitive type is encountered, it will cause an {@link IllegalArgumentException}.
     *
     * @param method  The method where the parameter originates from.
     * @param type    The type class of the {@link Argument} being built.
     * @param isArray A boolean representing whether the {@link Argument} is an array. This will affect the way default values are processed.
     * @param def     A {@link String} array containing the default values for the {@link Argument} being built.
     * @param builder The builder instance to be used for constructing the {@link Argument}.
     * @return The created nullable or optional {@link Argument}.
     * @throws IllegalArgumentException      If the {@link Argument} type is a primitive type but does not have a default value.
     * @throws ArgumentParserAbsentException If an ArgumentParser could not be retrieved for the {@link Argument} type.
     */
    private <T> Argument<T, S> createOptional(@NotNull Method method, @NotNull Class<T> type, ArgumentType isArray, @NotNull String[] def, @NotNull Argument.ArgumentBuilder<T, S> builder) {

        if (type.isPrimitive() && def.length == 0) {
            throw new IllegalArgumentException(String.format("Use wrappers instead of primitive types for nullability, %s.", method.getName()));
        }

        final ArgumentParser<?, ?> parser = getCommandImplementation().getParserList().get(type);
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
     * Creates an array of objects by using an {@link ArgumentParser} to deserialize a given array of strings into specific types.
     * <p>
     * This function is primarily used in the construction of nullable or optional {@link Argument}s that have an array of default values.
     * <p>
     * Function iterates over the given array of string defaults, using the provided {@link ArgumentParser} to deserialize each string into an instance of the specified type.
     * An array of these instances is then created and returned.
     *
     * @param type    The class object of the type that each element in the returned array should be.
     * @param adapter The {@link ArgumentParser} to be used for deserializing each string in the default values array.
     * @param def     The array of string default values to be deserialized into specific types.
     * @return An array of objects, where each object is an instance of the specified type.
     */
    private Object[] createArray(Class<?> type, ArgumentParser<?, ?> adapter, String[] def) {
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
     * If the array parameter is not in the right position, an exception of type {@link CommandException} is thrown.
     *
     * @param parameters The array of method's parameters.
     * @param isArray    A boolean indicating whether the parameter to validate is an array.
     * @param parameter  The parameter to validate.
     * @throws CommandException If the array parameter is not the last in the list.
     */
    private void validateArrayPlacement(@NotNull Parameter[] parameters, boolean isArray, @NotNull Parameter parameter) {
        if (isArray && parameter != parameters[parameters.length - 1]) {
            throw new CommandException("Please ensure that arrays are placed as the final argument, in accordance with the required syntax.", new IllegalArgumentException());
        }
    }
}
