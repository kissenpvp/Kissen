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

import net.kissenpvp.core.api.reflection.ReflectionClass;
import net.kissenpvp.core.api.reflection.ReflectionImplementation;
import net.kissenpvp.core.api.reflection.ReflectionPackage;

import java.io.File;


public class KissenReflectionImplementation implements ReflectionImplementation {
    @Override
    public ReflectionClass loadClass(Object instance, Class<?> clazz) {
        return new KissenReflectionClass(instance, clazz);
    }

    @Override
    public ReflectionClass loadClass(Class<?> clazz) {
        return new KissenReflectionClass(clazz);
    }

    @Override
    public ReflectionClass loadClass(Object instance) {
        return new KissenReflectionClass(instance);
    }

    @Override
    public ReflectionClass loadClass(String clazz) throws ClassNotFoundException {
        return new KissenReflectionClass(clazz);
    }

    @Override
    public ReflectionPackage loadPackage(String packagePath, File file) {
        return new KissenReflectionPackage(packagePath, file);
    }

    @Override
    public ReflectionPackage loadPackage(String packagePath, File file, ClassLoader classLoader) {
        ReflectionPackage reflectionPackage = loadPackage(packagePath, file);
        reflectionPackage.setClassLoader(classLoader);
        return reflectionPackage;
    }
}
