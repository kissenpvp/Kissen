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

package net.kissenpvp.core.api.reflection;

import net.kissenpvp.core.api.base.Implementation;

import java.io.File;


public interface ReflectionImplementation extends Implementation {

    /**
     * This constructor creates a class from a
     * plain java class and provides helpful
     * functions that are often needed when
     * working with JavaReflection.
     * <p>
     * Creates a class core with specification of the class. The instance is also transferred here immediately.
     *
     * @param instance The instance to be set.
     * @param clazz    The class from which the reflection help is needed.
     */
    ReflectionClass loadClass(Object instance, Class<?> clazz);

    /**
     * This constructor creates a class from a
     * plain java class and provides helpful
     * functions that are often needed when
     * working with JavaReflection.
     *
     * @param clazz The class from which the reflection help is needed.
     * @see Class
     */
    ReflectionClass loadClass(Class<?> clazz);

    /**
     * This constructor creates a class from a
     * plain java class and provides helpful
     * functions that are often needed when
     * working with JavaReflection.
     * <p>
     * Creates a class core based on an instance and its class.
     *
     * @param instance The instance to be set.
     */
    ReflectionClass loadClass(Object instance);

    /**
     * Creates a reflection class based on its position, which must be specified as follows:
     * If you want to create a class that is a <code>String</code>,
     * the argument must be called <code>java.lang.String</code>.
     * Only <code>String</code> is not allowed. Also, it's case-sensitive.
     *
     * @param clazz The class from which the reflection help is needed.
     * @throws ClassNotFoundException If the give path does not exist.
     */
    ReflectionClass loadClass(String clazz) throws ClassNotFoundException;

    ReflectionPackage loadPackage(String packagePath, File file);

    ReflectionPackage loadPackage(String packagePath, File jarFile, ClassLoader classLoader);
}
