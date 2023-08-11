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
