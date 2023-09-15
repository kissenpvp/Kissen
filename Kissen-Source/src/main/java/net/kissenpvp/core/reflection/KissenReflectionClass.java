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
import net.kissenpvp.core.api.reflection.Parameter;
import net.kissenpvp.core.api.reflection.ReflectionClass;
import net.kissenpvp.core.base.KissenCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class KissenReflectionClass implements ReflectionClass {
    private Class<?> clazz;
    @Getter
    @Setter
    private Object instance;
    @Getter
    @Setter
    private ClassLoader classLoader;

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
    public KissenReflectionClass(Object instance, Class<?> clazz, ClassLoader classLoader) {
        update(instance, clazz, classLoader);
    }

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
    public KissenReflectionClass(Object instance, Class<?> clazz) {
        this(instance, clazz, ClassLoader.getSystemClassLoader());
    }

    /**
     * This constructor creates a class from a
     * plain java class and provides helpful
     * functions that are often needed when
     * working with JavaReflection.
     *
     * @param clazz The class from which the reflection help is needed.
     * @see Class
     */
    public KissenReflectionClass(Class<?> clazz, ClassLoader classLoader) {
        this(null, clazz, classLoader);
    }

    /**
     * This constructor creates a class from a
     * plain java class and provides helpful
     * functions that are often needed when
     * working with JavaReflection.
     *
     * @param clazz The class from which the reflection help is needed.
     * @see Class
     */
    public KissenReflectionClass(Class<?> clazz) {
        this(clazz, ClassLoader.getSystemClassLoader());
    }

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
    public KissenReflectionClass(Object instance, ClassLoader classLoader) {
        this(instance, instance.getClass(), classLoader);
    }

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
    public KissenReflectionClass(Object instance) {
        this(instance, instance.getClass(), ClassLoader.getSystemClassLoader());
    }

    /**
     * Creates a reflection class based on its position, which must be specified as follows:
     * If you want to create a class that is a <code>String</code>,
     * the argument must be called <code>java.lang.String</code>.
     * Only <code>String</code> is not allowed. Also, it's case-sensitive.
     *
     * @param clazz The class from which the reflection help is needed.
     * @throws ClassNotFoundException If the give path does not exist.
     */
    public KissenReflectionClass(String clazz, ClassLoader classLoader) throws ClassNotFoundException {
        this(classLoader.loadClass(clazz), classLoader);
    }

    /**
     * Creates a reflection class based on its position, which must be specified as follows:
     * If you want to create a class that is a <code>String</code>,
     * the argument must be called <code>java.lang.String</code>.
     * Only <code>String</code> is not allowed. Also, it's case-sensitive.
     *
     * @param clazz The class from which the reflection help is needed.
     * @throws ClassNotFoundException If the give path does not exist.
     */
    public KissenReflectionClass(String clazz) throws ClassNotFoundException {
        this(Class.forName(clazz), ClassLoader.getSystemClassLoader());
    }


    @Override
    public ReflectionClass update(Object instance, Class<?> clazz, ClassLoader classLoader) {
        this.clazz = clazz;
        this.instance = instance;
        this.classLoader = classLoader;
        return this;
    }

    @Override
    public ReflectionClass update(Object instance, Class<?> clazz) {
        return update(instance, clazz, ClassLoader.getSystemClassLoader());
    }

    @Override
    public ReflectionClass update(Class<?> clazz, ClassLoader classLoader) {
        return update(null, clazz, ClassLoader.getSystemClassLoader());
    }

    @Override
    public ReflectionClass update(Class<?> clazz) {
        return update(null, clazz, ClassLoader.getSystemClassLoader());
    }

    @Override
    public ReflectionClass update(Object instance, ClassLoader classLoader) {
        return update(instance, instance.getClass());
    }

    @Override
    public ReflectionClass update(Object instance) {
        return update(instance, instance.getClass(), ClassLoader.getSystemClassLoader());
    }

    @Override
    public Object execute(String method, Parameter<?>... parameters) {
        try {
            return executeUnsafe(method, parameters);
        } catch (Exception exception) {
            KissenCore.getInstance().getLogger().error("An error occurred when executing method '{}' from object '{}'.", method, instance, exception);
        }
        return null;
    }

    @Override
    public Object executeUnsafe(String method, Parameter<?>... parameters) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        return invoke(false, method, parameters);
    }

    @Override
    public void setField(String field, Object newValue) {
        try {

            if (this.instance == null) {
                throw new NullPointerException("Instance not initialised");
            }

            final Field FIELD = this.instance.getClass().getDeclaredField(field);
            FIELD.setAccessible(true);
            FIELD.set(this.instance, newValue);
            FIELD.setAccessible(false);
        } catch (Exception exception) {
            KissenCore.getInstance().getLogger().error("An error occurred while setting field '{}' from object '{}'.", field, instance, exception);
        }
    }

    @Override
    public Object getField(String field) {
        try {
            if (this.instance == null) {
                throw new NullPointerException("Instance not initialised");
            }

            final Field FIELD = this.instance.getClass().getDeclaredField(field);
            FIELD.setAccessible(true);
            final Object RETURN = FIELD.get(this.instance);
            FIELD.setAccessible(false);

            return RETURN;

        } catch (Exception exception) {
            KissenCore.getInstance().getLogger().error("An error occurred while getting field '{}' from object '{}'.", field, instance, exception);
        }
        return null;
    }

    @Override
    public <T> T getField(Class<T> clazz) {
        for (Field currentField : getJavaClass().getDeclaredFields()) {
            if (currentField.getType().getName().equals(clazz.getName())) {
                if (getField(currentField.getName()) == null) {
                    break;
                }

                return (T) getField(currentField.getName());
            }
        }
        return null;
    }

    @Override
    public @NotNull @Unmodifiable Set<Object> searchField(Class<?> type) {
        return searchField(type.getName());
    }

    @Override
    public @NotNull @Unmodifiable Set<Object> searchField(String className) {
        Set<Object> FIELDS = new HashSet<>();
        for (Field declaredField : getJavaClass().getDeclaredFields()) {
            if (declaredField.getType().getName().equals(className) || declaredField.getType().getSimpleName().equals(className)) {
                FIELDS.add(getField(declaredField.getName()));
            }
        }
        return Collections.unmodifiableSet(FIELDS);
    }

    @Override
    public void setStatic(String field, Object newValue) {
        try {
            final Field FIELD = this.getJavaClass().getDeclaredField(field);
            FIELD.setAccessible(true);
            FIELD.set(null, newValue);
            FIELD.setAccessible(false);

        } catch (Exception exception) {
            KissenCore.getInstance().getLogger().error("An error occurred while setting static field '{}' from class '{}'.", field, clazz, exception);
        }
    }

    @Override
    public Object getStatic(String field) {
        try {
            final Field FIELD = this.getJavaClass().getDeclaredField(field);

            FIELD.setAccessible(true);
            final Object RETURN = FIELD.get(null);
            FIELD.setAccessible(false);

            return RETURN;

        } catch (Exception exception) {
            KissenCore.getInstance().getLogger().error("An error occurred while getting static field '{}' from class '{}'.", field, clazz, exception);
        }
        return null;
    }

    @Override
    public Object newInstance(Parameter<?>... parameters) {
        try {
            if (isInterface()) {
                KissenCore.getInstance().getLogger().info("You cannot create an instance of an interface.");
                return null;
            }

            if (parameters.length > 0) {
                setInstance(invoke(parameters));
            } else {
                newInstance();
            }

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException exception) {
            KissenCore.getInstance().getLogger().error("An error occurred while creating a new instance of the class '{}'.", clazz.getSimpleName(), exception);
        }
        return this.getInstance();
    }

    @Override
    public Object newInstance() {
        try {
            if (isInterface()) {
                KissenCore.getInstance().getLogger().info("You cannot create an instance of an interface.");
                return null;
            }

            if (this.getJavaClass().getSimpleName().trim().isEmpty()) {
                KissenCore.getInstance().getLogger().info("YCannot invoke to an empty class.");
                return null;
            }

            Class<?> javaClass = this.getJavaClass();

            Constructor<?> constructor;

            if (javaClass.getConstructors().length > 0) {
                constructor = javaClass.getConstructors()[0];
            } else {
                if (javaClass.getDeclaredConstructors().length > 0) {
                    constructor = javaClass.getDeclaredConstructors()[0];
                } else {
                    throw new NullPointerException("No constructor was found.");
                }
            }

            Object[] objects = new Object[constructor.getParameterCount()];

            for (int i = 0; i < constructor.getParameterCount(); i++) {
                objects[i] = getDefault(constructor.getParameterTypes()[i]);
            }

            constructor.setAccessible(true);
            Object callBack = constructor.newInstance(objects);

            if (Modifier.isPrivate(constructor.getModifiers()) || Modifier.isProtected(constructor.getModifiers())) {
                constructor.setAccessible(false);
            }

            setInstance(callBack);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            KissenCore.getInstance().getLogger().error("An error occurred while invoking empty constructor from class '{}'.", getJavaClass().getSimpleName(), exception);
        }
        return this.getInstance();
    }

    @Override
    public Package getPackage() {
        return null;
    }

    @Override
    public Class<?> getJavaClass() {
        return this.clazz;
    }

    @Override
    public boolean isInterface() {
        return Modifier.isInterface(getJavaClass().getModifiers());
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(getJavaClass().getModifiers());
    }

    /**
     * Turns the given parameters in a new instance of the object.
     *
     * @param parameters The parameter given to create this new instance.
     * @return The object that was created.
     * @throws InvocationTargetException When the constructor do not exist.
     * @throws NoSuchMethodException     Thrown by the method this is using when a method is non-existing.
     * @throws InstantiationException    When the object is not suitable for creating instances, like it is an
     *                                   interface.
     * @throws IllegalAccessException    When the constructor is private.
     */
    private Object invoke(Parameter<?>... parameters) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return invoke(true, "", parameters);
    }

    /**
     * Turns the given parameters in a new instance of the object,
     * or calls a method in an object.
     *
     * @param constructor Whether it should invoke a constructor or a method.
     * @param name        The sender of the method to be invoked.
     * @param parameters  The parameter given to create this new instance.
     * @return The object that was created or returned.
     * @throws InvocationTargetException When the constructor or method do not exist.
     * @throws NoSuchMethodException     Thrown by the method this is using when a method is non-existing.
     * @throws InstantiationException    When the object is not suitable for creating instances, like it is an
     *                                   interface.
     * @throws IllegalAccessException    When the constructor is private.
     */
    private Object invoke(boolean constructor, String name, Parameter<?>... parameters) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (parameters.length > 0 || constructor) {
            final Class<?>[] PARAMETER_TYPES = new Class<?>[parameters.length];
            final Object[] PARAMETERS = new Object[PARAMETER_TYPES.length];
            for (int i = 0; i < parameters.length; i++) {
                PARAMETER_TYPES[i] = parameters[i].type();
                PARAMETERS[i] = parameters[i].value();
            }
            if (constructor) {
                Constructor<?> declaredConstructor = this.getJavaClass().getDeclaredConstructor(PARAMETER_TYPES);
                declaredConstructor.setAccessible(true);
                Object callBack = declaredConstructor.newInstance(PARAMETERS);

                if (Modifier.isPrivate(declaredConstructor.getModifiers()) || Modifier.isProtected(declaredConstructor.getModifiers())) {
                    declaredConstructor.setAccessible(false);
                }

                return callBack;
            } else {
                for (Method currentMethod : this.instance.getClass().getMethods()) {
                    Object invoked = searchMethod(currentMethod, name, PARAMETER_TYPES, PARAMETERS);
                    if (invoked != null) {
                        return invoked;
                    }
                }
            }
        } else {
            return this.instance.getClass().getMethod(name).invoke(this.instance);
        }
        return null;
    }

    /**
     * Searches for a method with the given parameters.
     *
     * @param currentMethod  The method to be checked.
     * @param name           The sender of the method searched.
     * @param parameterTypes The types of the method.
     * @param parameter      The parameter values.
     * @return The return value of the invoked method.
     */
    private Object searchMethod(Method currentMethod, String name, Class<?>[] parameterTypes, Object[] parameter) {
        if (!currentMethod.getName().equals(name)) {
            return null;
        }
        try {
            if (currentMethod.getParameterCount() != parameter.length) {
                return null;
            }
            boolean skip = false;
            for (int i = 0; i < currentMethod.getParameterCount(); i++) {
                if (!currentMethod.getParameterTypes()[i].isAssignableFrom(parameterTypes[i])) {
                    skip = true;
                }
            }

            if (skip) {
                return null;
            }
            return currentMethod.invoke(this.instance, parameter);
        } catch (ArrayIndexOutOfBoundsException | IllegalAccessException | InvocationTargetException ignored) {
        }
        return null;
    }

    /**
     * Returns the default values for unknown variables.
     *
     * @param clazz The clazz that the parameter has.
     * @return The value that should be parsed through.
     */
    private Object getDefault(Class<?> clazz) {
        return switch (clazz.getSimpleName().toLowerCase()) {
            case "long", "int", "short", "byte", "float", "double" -> 0;
            case "boolean" -> false;
            case "char" -> '\0';
            default -> null;
        };
    }
}
