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
