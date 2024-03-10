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

package net.kissenpvp.core.database;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A contract defining the behavior of a data writer responsible for updating records.
 *
 * <p>The {@code DataWriter} interface provides methods to validate the immutability of the object and to update records.
 * It includes a constant {@code IMMUTABLE} representing the message for an immutable object.</p>
 *
 * @param <T> the type of records that this data writer can handle, extending the {@link Record} interface
 * @see Record
 */
public interface DataWriter<T extends Record> {

    String IMMUTABLE = "This object is immutable.";

    /**
     * Validates the immutability of the specified {@link DataWriter} object.
     *
     * <p>The {@code validate} method checks if the given {@link DataWriter} is not null. If the object is null,
     * it throws an {@link UnsupportedOperationException} with the message {@code IMMUTABLE}.</p>
     *
     * @param dataWriter the {@link DataWriter} to be validated
     * @throws UnsupportedOperationException if the specified {@link DataWriter} is null
     * @see #validate(DataWriter, RuntimeException)
     */
    static void validate(@Nullable DataWriter<?> dataWriter) {
        validate(dataWriter, new UnsupportedOperationException(IMMUTABLE));
    }

    /**
     * Validates the immutability of the specified {@link DataWriter} object, throwing a custom runtime exception
     * if the object is null.
     *
     * <p>The {@code validate} method checks if the given {@link DataWriter} is not null. If the object is null,
     * it throws the specified runtime exception.</p>
     *
     * @param dataWriter      the {@link DataWriter} to be validated
     * @param runtimeException the custom runtime exception to be thrown if the {@link DataWriter} is null
     * @param <T>             the type of runtime exception
     * @throws T if the specified {@link DataWriter} is null
     * @see #validate(DataWriter)
     */
    static <T extends RuntimeException> void validate(@Nullable DataWriter<?> dataWriter, @NotNull T runtimeException) {
        if (dataWriter==null) {
            throw runtimeException;
        }
    }

    /**
     * Updates the specified record using this data writer.
     *
     * <p>The {@code update} method is responsible for updating the given record. The behavior is defined by
     * concrete implementations of this interface.</p>
     *
     * @param record the record to be updated
     */
    void update(@NotNull T record);
}
