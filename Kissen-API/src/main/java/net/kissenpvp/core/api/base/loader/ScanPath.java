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

package net.kissenpvp.core.api.base.loader;

import net.kissenpvp.core.api.base.plugin.KissenPlugin;

import java.util.jar.JarFile;

/**
 * The {@code ScanPath} record represents a single path in the list of paths to scan using the {@link ClassScanner}.
 *
 * <p>
 * A {@code ScanPath} contains information about the JAR file that the class was found in,
 * the fully qualified name of the class, the {@link KissenPlugin} that the class belongs to,
 * the path to the class file within the JAR, and the class loader that loaded the class.
 * </p>
 *
 * <p>
 * This record is immutable and can be used to define a search path and load it using the {@link ClassScanner}.
 * </p>
 *
 * @param jarFile      the JAR file that contains the class file
 * @param name         the fully qualified name of the class
 * @param kissenPlugin the {@link KissenPlugin} that the class belongs to
 * @param path         the path to the class file within the JAR
 * @param classLoader  the class loader that loaded the class
 */
public record ScanPath(JarFile jarFile, String name, KissenPlugin kissenPlugin, String path,
                       ClassLoader classLoader) { }
