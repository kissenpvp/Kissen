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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a class should only be loaded by the {@link ClassScanner} if
 * {@link net.kissenpvp.core.api.base.DebugEnabled} is set to true.
 *
 * <p>
 * When this annotation is present on a class, the {@link ClassScanner} will only load the class
 * if the value of {@link net.kissenpvp.core.api.base.DebugEnabled} is true. This is useful for
 * classes that should only be loaded in a development or testing environment.
 * </p>
 *
 * <p>
 * This annotation can be applied to classes, interfaces, and enums.
 * </p>
 *
 * <p>
 * This annotation has a {@link Target} value of {@code ElementType.TYPE},
 * meaning that it can only be applied to classes, interfaces, and enums.
 * </p>
 *
 * <p>
 * This annotation has a {@link Retention} value of
 * {@code RetentionPolicy.RUNTIME}, meaning that it is retained at runtime and can be
 * queried at runtime using reflection.
 * </p>
 * @see net.kissenpvp.core.api.base.DebugEnabled
 */
@Target(value = { ElementType.TYPE }) @Retention(RetentionPolicy.RUNTIME) public @interface Debug { }
