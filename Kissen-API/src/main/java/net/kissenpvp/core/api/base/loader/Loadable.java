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
import net.kissenpvp.core.api.reflection.ReflectionClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

/**
 * This interface is used to define classes that can be loaded dynamically and to specify how they should be loaded and processed.
 * <p>
 * When implementing this interface, you should define how the class should be loaded and processed. The isLoadable method should be implemented to check if the class can be loaded, and the load method should be implemented to load the content of the class. The enable method should be implemented to start any processes that can only be executed after the server has started.
 * <p>
 * Optionally, the getInsert method can be implemented to return the object that is inserted into the list. The getComparator method can also be implemented to return a comparator used for sorting the list. If these methods are not implemented, default implementations will be used.
 * <p>
 * This interface is commonly used in plugin development to define classes that can be loaded and processed dynamically, allowing for greater flexibility and modularity in plugin design.
 * <p>
 * Note that this interface is automatically loaded by the {@link ClassScanner} itself. However, if you don't want it to load automatically, use the {@link Ignore} annotation.
 * @see ClassScanner
 */
public interface Loadable
{
    /**
     * Determines whether the specified ReflectionClass object can be loaded by the specified KissenPlugin object.
     * @param clazz The ReflectionClass object to be checked for loadability. Must not be null.
     * @param plugin The KissenPlugin object used for loading the class. Must not be null.
     * @return true if the class can be loaded by the plugin; false otherwise.
     * @throws NullPointerException if either the clazz or plugin parameter is null.
     * @see ReflectionClass
     * @see KissenPlugin
     */
    boolean isLoadable(@NotNull ReflectionClass clazz, @NotNull KissenPlugin plugin);

    /**
     * Loads the content of a Loadable object.
     * <p>
     * This method is responsible for loading the content of a Loadable object associated with a specified class and registered on a specified plugin. When implementing this method, you should define how the content should be loaded and processed.
     * <p>
     * The clazz parameter specifies the class associated with the Loadable object. This class should be the one whose content needs to be loaded.
     * <p>
     * The plugin parameter specifies the KissenPlugin object on which the Loadable object is registered. This plugin should be the one that is responsible for loading and processing the content of the Loadable object.
     * <p>
     * Implementations of this method should throw an exception if there is an error loading the content of the Loadable object.
     * <p>
     *     Note that this method is called before the plugins execute the pre start phase.
     * </p>
     * @param clazz the class associated with the Loadable object
     * @param plugin the KissenPlugin object on which the Loadable object is registered
     * @throws NullPointerException if either parameter is null
     */
    void load(@NotNull ReflectionClass clazz, @NotNull KissenPlugin plugin);

    /**
     * Returns the object to be added to the list when the class is loaded.
     * This method should be optionally implemented by classes implementing the Loadable interface.
     * If this method is not implemented, the default implementation will be used, which returns the instance of the specified class obtained using ReflectionClass.getInstance() method.
     *
     * @param clazz the ReflectionClass object associated with the loadable.
     * @return the object that is inserted into the list when the class is loaded.
     * @throws NullPointerException if the specified clazz parameter is null.
     */
    default @NotNull Object getInsert(@NotNull ReflectionClass clazz)
    {
        return clazz.getInstance();
    }

    /**
     * Returns a comparator for sorting the list. If this method is not implemented, the default implementation will return null, which indicates that the list will not be sorted.
     * <p>When implementing this method, you should define a comparator that is used for sorting the list. The comparator should be compatible with the type of the objects in the list.</p>
     * @return The comparator used for sorting the list, or null if the list should not be sorted.
     */
    default @Nullable Comparator<?> getComparator()
    {
        return null;
    }

    /**
     * This method is called by after the system is up and running to enable any processes that can only be executed after the server has started. The implementation of this method should start any such processes associated with the specified class.
     * @param clazz The ReflectionClass object associated with the loadable.
     * @param plugin The KissenPlugin object on which the loadable is registered.
     * @throws NullPointerException if either parameter is null.
     */
    void enable(@NotNull ReflectionClass clazz, @NotNull KissenPlugin plugin);
}
