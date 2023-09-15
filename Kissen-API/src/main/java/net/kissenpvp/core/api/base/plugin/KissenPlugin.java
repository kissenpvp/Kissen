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

package net.kissenpvp.core.api.base.plugin;

import net.kissenpvp.core.api.base.Implementation;
import net.kissenpvp.core.api.base.Kissen;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.MessageFormat;

/**
 * The {@link KissenPlugin} interface defines the methods that a plugins offer after being loaded. Note that this does NOT apply to modules.
 * <p>
 * The {@link KissenPlugin} interface provides methods for retrieving information about the plugin's file and package.
 * <p>
 * This interface is designed to be implemented by a class that represents a plugin in the Kissen system.
 * It can be used by other components of the system to interact with the plugin and manage its lifecycle.
 * <p>
 * This interface should be used in conjunction with the {@link Kissen} class, which represents the core of the Kissen application or server and provides methods for managing plugins.
 *
 * @see Implementation
 * @see Kissen
 */
public interface KissenPlugin extends Implementation {

    /**
     * Returns the name of the plugin. The name should be a short, human-readable string that identifies the plugin.
     * It is often used as a unique identifier for the plugin and may be displayed in various user interfaces.
     * The name should not contain any whitespace or special characters.
     * <p>
     * This method should always return a non-null value, even if the plugin has not been fully initialized or has been disabled.
     *
     * @return the name of the plugin.
     */
    @Subst("")
    @NotNull String getName();

    /**
     * Returns the full name of the plugin, including its name and version.
     * <p>
     * The name of the plugin is returned by the {@link #getName()} method, and the version is specified in the plugin's
     * {@code plugin.yml} file or the modules meta. The full name is returned as a string with the format "[Name] v[Version]".
     * <p>
     * The returned string is guaranteed to be non-null and non-empty.
     *
     * @return the full name of the plugin, including its name and version.
     */
    @NotNull String getFullName();

    @NotNull String getPackage();

    /**
     * Returns the folder containing the plugin assets, such as configuration files, language files.
     * This folder is located in the plugin's directory and should be created by the plugin during its initialization phase.
     * <p>
     * The returned {@code File} object represents the directory containing the plugin assets, and should not include the
     * plugin's directory itself. The returned file object should not be null, and should exist on the file system.
     * <p>
     * Note that the plugin assets may be loaded dynamically by the plugin or by other components of the system, and may
     * change during the runtime of the plugin.
     *
     * @return the folder containing plugin assets.
     */
    @NotNull File getDataFolder();

    /**
     * Returns the Kissen instance associated with this plugin.
     * This method is used to retrieve the instance of the Kissen class that is associated with this plugin.
     *
     * @return The Kissen instance.
     */
    @NotNull Kissen getKissen();

    void registerTranslation(@NotNull String key, @NotNull MessageFormat defaultMessage);
}
