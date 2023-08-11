package net.kissenpvp.core.command;

import net.kissenpvp.core.api.base.Implementation;
import net.kissenpvp.core.api.command.ArgumentParser;
import net.kissenpvp.core.api.command.exception.TemporaryDeserializationException;
import net.kissenpvp.core.command.argument.parser.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class KissenCommandImplementation implements Implementation {

    private final Map<Class<?>, ArgumentParser<?>> parserList;

    public KissenCommandImplementation() {
        this.parserList = new HashMap<>();
        parserList.put(String.class, new StringArgumentParser());
        parserList.put(Byte.class, new ByteArgumentParser());
        parserList.put(Short.class, new ShortArgumentParser());
        parserList.put(Integer.class, new IntegerArgumentParser());
        parserList.put(Float.class, new FloatArgumentParser());
        parserList.put(Double.class, new DoubleArgumentParser());
        parserList.put(Long.class, new LongArgumentParser());
        parserList.put(Character.class, new CharacterArgumentParser());

        //I hate java for this
        parserList.put(Byte.TYPE, new ByteArgumentParser());
        parserList.put(Short.TYPE, new ShortArgumentParser());
        parserList.put(Integer.TYPE, new IntegerArgumentParser());
        parserList.put(Float.TYPE, new FloatArgumentParser());
        parserList.put(Double.TYPE, new DoubleArgumentParser());
        parserList.put(Long.TYPE, new LongArgumentParser());
        parserList.put(Character.TYPE, new CharacterArgumentParser());
    }

    public <T> void registerParser(@NotNull Class<T> type, @NotNull ArgumentParser<T> parser) {
        if (!parserList.containsKey(type)) {
            parserList.put(type, parser);
            return;
        }
        throw new IllegalArgumentException("Type already exits"); //TODO better message
    }

    public @NotNull @Unmodifiable Map<Class<?>, ArgumentParser<?>> getParserList() {
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
    public @NotNull Object deserialize(@NotNull String input, @NotNull ArgumentParser<?> argumentParser) {
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
