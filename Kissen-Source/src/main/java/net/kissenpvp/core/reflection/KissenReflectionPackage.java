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

package net.kissenpvp.core.reflection;

import lombok.Getter;
import lombok.Setter;
import net.kissenpvp.core.api.reflection.ReflectionPackage;
import net.kissenpvp.core.base.KissenCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class KissenReflectionPackage implements ReflectionPackage {
    private final String packageName;
    private final File sourceFile;
    @Getter
    @Setter
    private ClassLoader classLoader;

    /**
     * Creates a new {@link KissenReflectionPackage} using the specified package path.
     *
     * <p>This constructor is used to create a reflection package object for the given package path within the project.
     * The package path should represent the position of the package in the project hierarchy.</p>
     *
     * @param packagePath The position of the package in the project. Must not be null.
     * @throws URISyntaxException If the package path is not a valid URI.
     * @throws IOException        If an I/O error occurs while creating the package.
     */
    public KissenReflectionPackage(@NotNull String packagePath) throws URISyntaxException, IOException {

        this(packagePath, KissenReflectionPackage.class, ClassLoader.getSystemClassLoader());
    }


    /**
     * Creates a new {@link KissenReflectionPackage} using the specified package path and root class.
     *
     * <p>This constructor is used to create a reflection package object for the given package path within the project.
     * The package path should represent the position of the package in the project hierarchy.</p>
     *
     * <p>The root class parameter is used to determine the location of the JAR file from which the classes will be retrieved.
     * It is necessary to provide a root class that belongs to the same project or JAR file containing the desired package.</p>
     *
     * @param packagePath The position of the package in the project. Must not be null.
     * @param root        The root class used to determine the JAR file location. Must not be null.
     * @throws URISyntaxException If the package path or root class location is not a valid URI.
     * @throws IOException        If an I/O error occurs while creating the package.
     */
    public KissenReflectionPackage(@NotNull String packagePath, @NotNull Class<?> root) throws URISyntaxException, IOException {

        this(packagePath, root, ClassLoader.getSystemClassLoader());
    }

    /**
     * Creates a package, this is for the first time only a sender, but with this you can get all classes in this itself.
     * Similar to {@link KissenReflectionClass} it can be called with dot. only the sender itself is not enough.
     * <p>
     * if you are using this class in another project, make sure to insert the right plugin file so that the plugin
     * recognizes the classes in this specific package.
     *
     * @param packagePath The position of the package in the project.
     * @param file        The file this reader is getting the classes from due spigot classloader is corrupt and make
     *                    things complicated.
     */
    public KissenReflectionPackage(@NotNull String packagePath, @NotNull File file) {
        this(packagePath, file, ClassLoader.getSystemClassLoader());
    }

    /**
     * Creates a new {@link KissenReflectionPackage} using the specified package path and class loader.
     *
     * <p>This constructor is used to create a reflection package object for the given package path within the project.
     * The package path should represent the position of the package in the project hierarchy.</p>
     *
     * @param packagePath The position of the package in the project. Must not be null.
     * @param classLoader The class loader to be used for loading classes. Must not be null.
     * @throws URISyntaxException If the package path is not a valid URI.
     * @throws IOException        If an I/O error occurs while creating the package.
     */
    public KissenReflectionPackage(@NotNull String packagePath, @NotNull ClassLoader classLoader) throws URISyntaxException, IOException {

        this(packagePath, KissenReflectionPackage.class, classLoader);
    }


    /**
     * Creates a new {@link KissenReflectionPackage} using the specified package path, root class, and class loader.
     *
     * <p>This constructor is used to create a reflection package object for the given package path within the project.
     * The package path should represent the position of the package in the project hierarchy.</p>
     *
     * <p>The root class parameter is used to determine the location of the JAR file from which the classes will be retrieved.
     * It is necessary to provide a root class that belongs to the same project or JAR file containing the desired package.</p>
     *
     * @param packagePath The position of the package in the project. Must not be null.
     * @param root        The root class used to determine the JAR file location. Must not be null.
     * @param classLoader The class loader to be used for loading classes. Must not be null.
     * @throws URISyntaxException If the package path or root class location is not a valid URI.
     * @throws IOException        If an I/O error occurs while creating the package.
     */
    public KissenReflectionPackage(@NotNull String packagePath, @NotNull Class<?> root, @NotNull ClassLoader classLoader) throws URISyntaxException, IOException {

        this(packagePath, new File(root.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()), classLoader);
    }

    /**
     * Creates a package, this is for the first time only a sender, but with this you can get all classes in this itself.
     * Similar to {@link KissenReflectionClass} it can be called with dot. only the sender itself is not enough.
     * <p>
     * if you are using this class in another project, make sure to insert the right plugin file so that the plugin
     * recognizes the classes in this specific package.
     *
     * @param packagePath The position of the package in the project.
     * @param file        The file this reader is getting the classes from due spigot classloader is corrupt and make
     *                    things complicated.
     * @param classLoader The class loader to be used for loading classes. Must not be null.
     */
    public KissenReflectionPackage(@NotNull String packagePath, @NotNull File file, @NotNull ClassLoader classLoader) {
        this.packageName = packagePath;
        this.sourceFile = file;
        this.classLoader = classLoader;
    }

    @Override
    public @NotNull String getPath() {
        return packageName;
    }

    @Override
    public @NotNull Optional<Class<?>> getClass(@NotNull String name) {
        try {
            return Optional.of(classLoader.loadClass((name.startsWith(packageName) ? "" : this.packageName) + name));
        } catch (ClassNotFoundException exception) {
            KissenCore.getInstance().getLogger().error("An error occurred while trying to get class '{}' out of package.", name, exception);
        }
        return Optional.empty();
    }

    @Override
    public @NotNull @Unmodifiable List<Class<?>> getClasses() {
        try {
            List<Class<?>> classes = new ArrayList<>();
            getUnsafeClasses().stream().map(this::getClass).filter(Optional::isPresent).forEach(clazz -> classes.add(clazz.get()));
            return List.copyOf(classes);
        } catch (Exception exception) {
            KissenCore.getInstance().getLogger().error("An error occurred while trying to read contents of the package '{}'.", packageName, exception);
        }
        return new ArrayList<>();
    }

    @Override
    public @Unmodifiable @NotNull List<String> getUnsafeClasses() {
        List<String> classes = new ArrayList<>();
        try {
            Set<String> stringPaths = new HashSet<>();
            if (!sourceFile.isDirectory()) {
                try (JarFile jarFile = new JarFile(sourceFile.getPath())) {
                    for (Enumeration<JarEntry> entry = jarFile.entries(); entry.hasMoreElements(); ) {
                        stringPaths.add(entry.nextElement().getName());
                    }
                }
            } else {
                try (Stream<Path> stream = Files.walk(sourceFile.toPath())) {
                    stringPaths.addAll(stream.filter(file -> !file.toString().equals(sourceFile.getPath())).map(file -> file.toString().substring(sourceFile.getAbsolutePath().length() + 1)).collect(Collectors.toSet()));
                }
            }

            for (String path : stringPaths.stream().filter(path -> path.endsWith(".class")).map(path -> path.replace('/', '.')).collect(Collectors.toSet())) {
                if (path.startsWith(packageName) && path.endsWith(".class") && !path.contains("$") && !path.trim().isEmpty()) {
                    classes.add(path.substring(0, path.length() - 6));
                }
            }

        } catch (Exception exception) {
            KissenCore.getInstance().getLogger().error("An error occurred while trying to get jar file.", exception);
        }
        return Collections.unmodifiableList(classes);
    }

    @SafeVarargs
    @Override
    public final @NotNull @Unmodifiable <T> Set<T> getClasses(@NotNull Class<T>... assignable) {
        return this.getClasses().stream().filter(clazz -> Stream.of(assignable).anyMatch(assignClass -> assignClass.isAssignableFrom(clazz))).map(clazz -> (T) clazz).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Package getJavaPackage() {
        return getClassLoader().getDefinedPackage(packageName);
    }
}
