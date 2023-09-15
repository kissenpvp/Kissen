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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;


public interface ReflectionClass extends ReflectionEntry
{

    ReflectionClass update(Object instance, Class<?> clazz, ClassLoader classLoader);

    /**
     * Updates the instance loaded in this class.
     *
     * @param instance The instance that is set. This can be {@code null}.
     * @param clazz    The new {@link Class} this class should be about.
     * @return The instance after it was updated.
     */
    ReflectionClass update(Object instance, Class<?> clazz);


    ReflectionClass update(Class<?> clazz, ClassLoader classLoader);

    /**
     * Updates the instance loaded in this class.
     *
     * @param clazz The new {@link Class} this class should be about.
     * @return The instance after it was updated.
     */
    ReflectionClass update(Class<?> clazz);

    /**
     * Updates the {@link #getInstance()} loaded in this class.
     *
     * @param instance    the new instance as an object.
     * @param classLoader the class loader this object is from.
     * @return the instance after it was updated.
     */
    ReflectionClass update(Object instance, ClassLoader classLoader);

    /**
     * Updates the instance loaded in this class.
     *
     * @param instance The instance that is set. This can be {@code null}.
     * @return The instance after it was updated.
     */
    ReflectionClass update(Object instance);

    /**
     * Executes a method.
     * Can only be used if an instance has already been created or transferred.
     * In addition to {@link #executeUnsafe(String, Parameter[])} (String, Object...)} it does handle exceptions.
     *
     * @param method     The id of the method to be executed.
     * @param parameters The parameters to be passed.
     * @return The return value of the executed method.
     */
    Object execute(String method, Parameter<?>... parameters);

    /**
     * Executes a method.
     * Can only be used if an instance has already been created or transferred.
     *
     * @param method     The id of the method to be executed.
     * @param parameters The parameters to be passed.
     * @return The return value of the executed method.
     * @throws InvocationTargetException is thrown when the method produces errors or the parameters are wrong.
     * @throws IllegalAccessException    is thrown when the method isn't reachable by this class.
     * @throws NoSuchMethodException     is thrown when the class do not exist.
     */
    Object executeUnsafe(String method, Parameter<?>... parameters) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException;

    /**
     * This method edits private variables of objects.
     *
     * @param field    The id of the variable, that should be edited.
     * @param newValue The new value that will be set.
     */
    void setField(String field, Object newValue);

    /**
     * Get values of private variables.
     *
     * @param field The id of the variable, which should be revealed.
     * @return The value of the variable.
     */
    Object getField(String field);

    /**
     * Searches for a specific type of variable in a class and return its value.
     * When there are more variable with the same type, it'll return the first.
     *
     * @param field of variable to search for.
     * @return the value, that this variable contains.
     */
    <T> T getField(Class<T> field);

    @NotNull @Unmodifiable Set<Object> searchField(Class<?> type);

    @NotNull @Unmodifiable Set<Object> searchField(String className);

    /**
     * Set a static variable from a class. This must be {@code private}.
     *
     * @param field    The id of the variable.
     * @param newValue The new value of the variable.
     */
    void setStatic(String field, Object newValue);

    /**
     * Get a static variable from a class. This must be {@code private}.
     *
     * @param field The id of the variable.
     * @return The value of the static variable.
     */
    Object getStatic(String field);

    /**
     * Get the current instance of the class
     * can be null.
     *
     * @return The current instance.
     */
    Object getInstance();

    /**
     * Set an existing instance to the class instance
     *
     * @param instance The new instance of the class.
     */
    void setInstance(Object instance);

    /**
     * Add an instance to the class.
     * The instance can be null, to remove.
     *
     * @param parameters The parameters of the class.
     * @return The created instance of the class with the given parameters.
     */
    Object newInstance(Parameter<?>... parameters);

    /**
     * Creates an empty instance of the class.
     *
     * @return An instance of the class whose parameters are all set to {@code null}.
     */
    Object newInstance();

    /**
     * The package this class is in.
     *
     * @return The Package as reflection package.
     */
    Package getPackage();

    /**
     * Get the Class as java version and get other things.
     *
     * @return The java class of this class type.
     */
    Class<?> getJavaClass();

    /**
     * @return if it's an interface.
     */
    boolean isInterface();

    /**
     * @return whether it's abstract or not.
     */
    boolean isAbstract();
}
