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
