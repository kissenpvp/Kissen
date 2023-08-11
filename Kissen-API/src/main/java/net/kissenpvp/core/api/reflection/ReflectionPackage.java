/*
 * Copyright 2023 KissenPvP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.kissenpvp.core.api.reflection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface ReflectionPackage extends ReflectionEntry {


    /**
     * This method indicates which package it is.
     *
     * @return The path in the project, which marks where it shows its affiliation.
     */
    @NotNull String getPath();

    /**
     * Get a class in this package and load it
     *
     * @param name The id of the Class
     * @return The loaded Class.
     */
    @NotNull Optional<Class<?>> getClass(String name);

    /**
     * Get the classes that are in the package.
     *
     * @return The classes as a list.
     */
    @NotNull @Unmodifiable List<Class<?>> getClasses();

    /**
     * Get the classes that are in the package.
     *
     * @return The classes as a list.
     */
    @NotNull @Unmodifiable List<String> getUnsafeClasses();

    /**
     * Get the classes that are in the package.
     * And filter the classes, that are assignable to the given class.
     *
     * @param assignable The classes, which indicates what type they should be.
     * @return A list of the filtered classes, which are assignable to the given classes
     */
    <T> @NotNull @Unmodifiable Set<T> getClasses(Class<T>... assignable);

    /**
     * Get the Java package and its information.
     *
     * @return The package as a Java package.
     */
    Package getJavaPackage();
}
