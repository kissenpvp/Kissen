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

package net.kissenpvp.core.api.database.file;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.*;
import java.util.List;

/**
 * The `File` interface represents a file in a file system.
 * It provides methods for performing various operations on files, such as reading, writing, and manipulating file
 * properties.
 * This interface serves as a general abstraction for working with files and can be implemented by specific file
 * handling classes.
 *
 * <p>
 * The `File` interface provides methods to access and manipulate file-related information, such as the file path,
 * name, size, and modification timestamp.
 * It also allows for reading and writing file contents, appending data, deleting files, and checking file existence.
 * </p>
 *
 * <p>
 * Implementations of the `File` interface may represent different types of files, such as text files, binary files,
 * or configuration files.
 * They may also support additional features specific to certain file formats or file system characteristics.
 * </p>
 *
 * <p>
 * It is important to note that the behavior and functionality of the `File` interface may vary depending on the
 * underlying file system and the implementation.
 * Therefore, it is recommended to refer to the documentation of the specific implementation being used for detailed
 * information on its behavior and limitations.
 * </p>
 *
 * <p>
 * When using the `File` interface, it is essential to handle potential exceptions that may occur during file
 * operations.
 * Common exceptions to consider include `IOException` for general I/O errors, `FileNotFoundException` for when a
 * file does not exist,
 * `SecurityException` for permission-related issues, and `UnsupportedOperationException` for unsupported operations.
 * </p>
 *
 * <p>
 * The `File` interface does not prescribe the specific file system or storage medium being used, allowing for
 * flexibility in implementation.
 * It can be used to work with files stored locally on disk, in a distributed file system, or in other storage systems.
 * </p>
 *
 * @see File
 * @see java.nio.file.Path
 * @see IOException
 * @see FileNotFoundException
 * @see SecurityException
 * @see UnsupportedOperationException
 */
public interface FileEditor
{
    /**
     * Writes a serializable object to a file.
     * <p>
     * This method is used to write a serializable object to a file in a binary format.
     * The object to be written must implement the {@link Serializable} interface.
     *
     * <p>
     * The implementation of this method creates an {@link ObjectOutputStream} to write the object to the file.
     * It then flushes and closes the output stream to ensure that all data is written and any resources are released.
     * <p>It is important to note that the object being written must be serializable. Otherwise, a {@link NotSerializableException}
     * will be thrown during the serialization process.
     * <p>The file to which the object is written is specified either when constructing the implementing class or
     * through other means, such as setting a file path or providing a file object. Implementations should document
     * the specific requirements or considerations related to file handling and management.
     * </p>
     *
     * <p>
     * It is recommended to perform proper null-checking on the object parameter before calling this method to ensure
     * that a non-null object is provided. Passing a null object will result in a {@link NullPointerException} being thrown.
     * <p>CWhen using this method there should be any potential exceptions handled, such as {@link IOException}, that may occur
     * during the write operation. It is also advisable to consider appropriate error handling and resource cleanup,
     * such as releasing any locks on the file or handling disk space limitations.
     * </p>
     *
     * @param object the object to be written to the file. It must not be null.
     * @throws IOException if an I/O error occurs while writing the object to the file.
     *
     * @see #readObject(Class)
     * @see Serializable
     * @see ObjectOutputStream
     * @see FileOutputStream
     * @see IOException
     * @see NotSerializableException
     */
    <T extends Serializable> void writeObject(@NotNull T object) throws IOException;

    /**
     * Reads a serializable object from a file.
     * <p>
     * This method is used to read a serializable object from a file in a binary format.
     * The object type to be read is specified using the {@code type} parameter, which should be the class of the object.
     * The object class must implement the {@link Serializable} interface.
     *
     * <p>
     * The implementation of this method creates an {@link ObjectInputStream} to read the object from the file.
     * It then closes the input stream to release any resources associated with it.
     * <p>It is important to note that the object being read must be serializable and match the specified type.
     * Otherwise, a {@link ClassNotFoundException} or {@link ClassCastException} may be thrown during deserialization.
     * <p>The file from which the object is read is specified either when constructing the implementing class or
     * through other means, such as setting a file path or providing a file object. Implementations should document
     * the specific requirements or considerations related to file handling and management.
     * </p>
     *
     * <p>
     * It is recommended to perform proper null-checking on the `type` parameter before calling this method to ensure
     * that a non-null type is provided. Passing a null type will result in a {@link NullPointerException} being thrown.
     * <p>When using this method there should be any potential exceptions handled, such as {@link IOException},
     * {@link ClassNotFoundException}, or {@link ClassCastException}, that may occur during the read operation.
     * It is also advisable to consider appropriate error handling, such as checking for file existence or permissions,
     * and to handle the case when the file does not contain a compatible object or when the deserialized object cannot
     * be cast to the specified type.
     * </p>
     * @param <T> the type of the object being read, which must implement the {@link Serializable} interface.
     * @return the deserialized object of the specified type read from the file.
     * @throws IOException if an I/O error occurs while reading the object from the file.
     * @throws ClassNotFoundException if the class of the object being read is not found during deserialization.
     * @throws ClassCastException if the deserialized object cannot be cast to the specified type.
     *
     * @see #writeObject(Serializable)
     * @see Serializable
     * @see ObjectInputStream
     * @see FileInputStream
     * @see IOException
     * @see ClassNotFoundException
     * @see ClassCastException
     */
    <T extends Serializable> @NotNull T readObject(@NotNull Class<T> type) throws IOException, ClassNotFoundException;

    /**
     * Reads a JSON object from a file.
     * <p>
     * This method is used to read a JSON object from a file. The file is expected to contain valid JSON data.
     *
     * <p>
     * The implementation of this method creates a {@link BufferedReader} to read the file character by character.
     * It then uses a {@link FileReader} to open the file for reading. The file is specified by the path obtained from
     * the {@code getJavaFile().getAbsolutePath()} method, which should return the absolute path to the Java file associated
     * with this implementation.
     * <p>The method then uses a {@link JsonParser} to parse the reader and obtain a {@link JsonObject}.
     * <p>Finally, the method closes the reader to release any resources associated with it.
     * <p>It is important to note that the file being read must contain valid JSON data; otherwise, a parsing exception
     * may occur when attempting to convert the file contents into a JSON object.
     * </p>
     *
     * <p>
     * When using this method there should be any potential exceptions handled, such as {@link IOException}, that may occur
     * during the read operation. It is also advisable to consider appropriate error handling, such as checking for
     * file existence or permissions, and handling cases where the file does not contain valid JSON data or cannot be read.
     * </p>
     *
     * @return the JSON object read from the file.
     * @throws IOException if an I/O error occurs while reading the JSON from the file.
     * @see #writeJSON(JsonObject)
     * @see BufferedReader
     * @see FileReader
     * @see JsonObject
     * @see JsonParser
     * @see IOException
     */
    @NotNull JsonObject readJSON() throws IOException;

    /**
     * Writes a JSON object to a file.
     * <p>
     * This method is used to write a JSON object to a file. The JSON object is serialized and written in a formatted manner
     * to the file specified by the implementing class.
     *
     * <p>
     * The implementation of this method creates a {@link FileWriter} to write the JSON object to the file.
     * It then uses a {@link Gson} instance to serialize the JSON object and writes the serialized JSON as a formatted string
     * to the file. The file to which the JSON is written is specified by the implementing class.
     * <p>After writing the JSON object to the file, the method flushes and closes the writer to ensure all data is written
     * and any resources associated with it are released.
     * <p>It is important to note that the JSON object being written must be non-null and properly formatted.
     * </p>
     *
     * <p>
     * When using this method there should be any potential exceptions handled, such as {@link IOException}, that may occur
     * during the write operation. It is also advisable to consider appropriate error handling, such as checking for
     * file existence or permissions, and handling cases where the JSON object cannot be properly serialized or written
     * to the file.
     * </p>
     *
     * @param json the JSON object to be written to the file. It must not be null.
     * @throws IOException if an I/O error occurs while writing the JSON to the file.
     *
     * @see #readJSON()
     * @see FileWriter
     * @see Gson
     * @see IOException
     */
    void writeJSON(@NotNull JsonObject json) throws IOException;

    /**
     * Appends text to the end of the file.
     * <p>
     * This method is used to add text to the end of the file, without specifying a specific line number.
     * The text provided as input will be appended to the existing contents of the file.
     *
     * <p>
     * The implementation of this method opens the file in append mode and writes the text to the end of the file.
     * The specific file to which the text is written is determined by the implementing class.
     * <p>It is important to note that the text being written must be non-null.
     * </p>
     *
     * <p>
     * When using this method there should be any potential exceptions handled, such as {@link IOException},
     * that may occur during the write operation. It is also advisable to consider appropriate error handling,
     * such as checking for file existence or permissions, and handling cases where the text cannot be properly written
     * to the file.
     * </p>
     *
     * <p>
     *     Note that when using this method, the given text is appended at the end. If this should be somewhere
     *     specific in the file {@link #write(String, int)} does the trick.
     * </p>
     *
     * @param text the text to be added to the file. It must not be null.
     * @see IOException
     */
    void write(@NotNull String text);

    /**
     * Writes a line of text at a specific line number in the file.
     * <p>
     * This method is used to write a line of text at a specific line number in the file.
     * The line number is provided as input, and the text will be inserted at that line, shifting the existing
     * content if necessary.
     *
     * <p>
     * The implementation of this method opens the file and inserts the text at the specified line number.
     * If the line number exceeds the existing number of lines in the file, the text will be appended to the end of
     * the file.
     * The specific file to which the text is written is determined by the implementing class.
     * <p>It is important to note that the text being written must be non-null, and the line number must be a
     * positive integer.
     * </p>
     *
     * <p>
     * When using this method there should be any potential exceptions handled, such as {@link IOException},
     * that may occur during the write operation. It is also advisable to consider appropriate error handling,
     * such as checking for file existence or permissions, and handling cases where the text cannot be properly written
     * to the file or when an invalid line number is provided.
     * </p>
     *
     * @param text the text to be written to the file. If null, the line will be deleted.
     * @param line the line number at which the text should be inserted. It must be a positive integer.
     * @see IOException
     */
    void write(@Nullable String text, int line);

    /**
     * Writes a value associated with a key.
     * To remove a value, set it to null.
     *
     * <p>
     * This method is used to write a value in association with a specific key.
     * The key and value parameters are provided as input, and the value will be stored or updated for the
     * corresponding key.
     * If the value is set to null, the existing value associated with the key will be removed.
     * </p>
     *
     * <p>
     * Additional comments or notes can be provided through the comments parameter, allowing for documentation
     * or contextual information related to the key-value pair.
     * </p>
     *
     * <p>
     * It is important to note that the key parameter must not be null, otherwise a NullPointerException will be thrown.
     * The value parameter can be null to indicate the removal of a value associated with the key.
     * </p>
     *
     * <p>
     * Potential exceptions, such as IOException, may occur during the write operation.
     * It is advisable to handle these exceptions appropriately.
     * Additionally, consider appropriate error handling, such as checking for file existence or permissions,
     * and handling cases where the value cannot be properly written or when an invalid key is provided.
     * </p>
     *
     * @param key      The key associated with the value.
     * @param value    The value to be written. If null, the value will be removed.
     * @param comments Additional comments or notes related to the key-value pair.
     * @throws NullPointerException If the key parameter is null.
     * @see IOException
     */
    void write(@NotNull String key, @Nullable Object value, @NotNull String... comments);

    /**
     * Empty the contents of the file, effectively clearing it.
     *
     * <p>
     * This method is used to empty the contents of the file, effectively clearing it.
     * All existing data in the file will be deleted, resulting in an empty file.
     * </p>
     *
     * <p>
     * It is important to note that this operation cannot be undone.
     * Once the file is cleared, all previous data will be permanently lost.
     * </p>
     *
     * <p>
     * It is recommended to exercise caution when using this method, as it directly modifies the file's content.
     * Before calling this method, ensure that you have taken appropriate measures to backup or save any important data
     * that may be lost during the clearing process.
     * </p>
     */
    void clear();

    /**
     * Read the contents of the file and return them as an unmodifiable list of strings.
     *
     * <p>
     * This method is used to read the contents of the file and return them as an unmodifiable list of strings.
     * Each line in the file will be represented as a separate element in the returned list.
     * </p>
     *
     * <p>
     * The returned list is unmodifiable, meaning that any attempt to modify its elements or structure will result in
     * an UnsupportedOperationException.
     * This ensures that the integrity of the read data is maintained and prevents unintended modifications.
     * </p>
     *
     * <p>
     * Potential exceptions, such as IOException, may occur during the read operation.
     * It is advisable to handle these exceptions appropriately.
     * Additionally, consider appropriate error handling, such as checking for file existence or permissions,
     * and handling cases where the file cannot be read.
     * </p>
     *
     * @return an unmodifiable list of strings representing the contents of the file.
     * @see UnsupportedOperationException
     */
    @NotNull @Unmodifiable List<String> read();

    /**
     * Get the text of a certain line.
     *
     * <p>
     * This method is used to retrieve the text of a specific line in the file.
     * The line number is provided as input, and the text of the corresponding line will be returned.
     * </p>
     *
     * <p>
     * It is important to note that the line parameter must be a valid line number within the file.
     * If the line number exceeds the total number of lines in the file, an IndexOutOfBoundsException will be thrown.
     * </p>
     *
     * @param line The line whose text is to be returned.
     * @return The text of the given line.
     * @throws IndexOutOfBoundsException if the line parameter is not a valid line number within the file.
     */
    @NotNull String read(int line);

    /**
     * Read the contents of a specific key.
     *
     * <p>
     * This method is used to retrieve the contents associated with a specific key in the file.
     * The key parameter is provided as input, and the content associated with the key will be returned.
     * </p>
     *
     * <p>
     * It is important to note that the key parameter must not be null, otherwise a NullPointerException will be thrown.
     * </p>
     *
     * @param key the key whose content is to be read.
     * @return The content of the key.
     *
     * @throws NullPointerException if the key parameter is null.
     */
    @NotNull String read(@NotNull String key);

    /**
     * This method returns a list.
     *
     * <p>
     * This method is used to retrieve a list associated with a specific key in the file.
     * The key parameter is provided as input, and the list associated with the key will be returned.
     * </p>
     *
     * <p>
     * It is important to note that the key parameter must not be null, otherwise a NullPointerException will be thrown.
     * The returned list will contain strings that are not null.
     * </p>
     *
     * @param key The key behind which the list is.
     * @return The list.
     *
     * @throws NullPointerException if the key parameter is null.
     */
    @NotNull List<@NotNull String> readList(@NotNull String key);

    /**
     * Get the file as a Java file.
     *
     * <p>
     * This method is used to obtain the file represented as a Java `File` object.
     * The returned `File` object can be used for various file operations provided by the `java.io.File` class.
     * </p>
     *
     * @return The file as a Java `File` object.
     */
    @NotNull File getJavaFile();
}
