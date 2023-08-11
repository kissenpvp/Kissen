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

package net.kissenpvp.core.api.base;

/**
 * The {@link Implementation} interface specifies the methods that a system component should implement,
 * serving as a contract for defining the behavior and capabilities of a system component.
 *
 * <p>
 * The {@link Implementation} interface provides a set of methods for managing and interacting with the component.
 * The specific methods to be implemented will depend on the nature and purpose of the component.
 *
 * <p>
 * This interface should be implemented by classes that represent system components and provide their functionality.
 * It allows other components of the system to interact with and utilize the capabilities of the component.
 *
 * <p>
 * It is recommended to define additional interfaces that extend {@link Implementation} to specify more specialized
 * functionality.
 * By using these interfaces, components can indicate support for specific features or behaviors.
 *
 * <p>
 * This interface should be used as a foundation for building system components and defining their contracts.
 * Implementations of this interface should thoroughly document the specific behavior and requirements of the component.
 *
 * <p>
 * All implementations can be retrieved using the {@link Kissen#getImplementation(Class)} method.
 * Please refer to the system manual you are using for instructions on how to access the Kissen instance.
 *
 * <p>
 * Example:
 * <pre>{@code
 * public class MyComponent implements Implementation
 * {
 *     // ... implement required methods and add additional functionality
 * }
 * }</pre>
 */
public interface Implementation {

    /**
     * The {@code preStart()} method is called during the server startup process before the component is fully
     * initialized.
     * Unlike the {@link #start()} and {@link #postStart()} methods, there is a high chance that certain aspects are
     * not yet fully initialized.
     * This method is intended to be used for loading necessary resources or performing any setup required before the
     * {@link #start()} method is called.
     *
     * <p>
     * Upon invoking the {@code preStart()} method, it should execute the necessary operations and return a boolean
     * value indicating the success or failure of the pre-start phase.
     * If the pre-start phase encounters any critical internal failures or errors, it should return {@code false}.
     * In such cases, the plugin should disable itself to prevent further issues and ensure system stability.
     *
     * <p>
     * This method should be overridden by implementing classes if any pre-start tasks are required.
     * It is recommended to document any specific behavior or requirements of the component during the pre-start phase.
     * </p>
     *
     * <p>
     * The default implementation of this method returns {@code true}.
     * Implementing classes should override this method and provide the necessary logic based on their requirements.
     * </p>
     *
     * <p>
     * Implementing classes should ensure proper error handling and provide meaningful error messages or logging if
     * the pre-start phase fails.
     * Additionally, any necessary cleanup or rollback operations should be performed if applicable.
     * </p>
     *
     * <p>
     * This method should be invoked by the system during the server startup process. It should not be called manually.
     * The order of execution of the pre-start phase among different components may vary depending on the specific
     * system configuration and requirements.
     * </p>
     *
     * @return {@code true} if the pre-start phase was successful and all required operations completed without errors.
     * {@code false} if the pre-start phase encountered failures or errors that require the plugin to disable itself.
     * @see #start()
     * @see #postStart()
     */
    default boolean preStart() {
        return true;
    }

    /**
     * The {@code start()} method is called during the server startup process to initialize and start the component.
     * It is invoked after the {@link #preStart()} method and when the system is fully initialized.
     *
     * <p>
     * Upon invoking the {@code start()} method, the component should perform any necessary initialization steps and
     * start its functionality.
     * The start method should return a boolean value indicating the success or failure of the start phase.
     * If any critical errors or failures occur during the start phase, the method should return {@code false}.
     * In such cases, the plugin may choose to disable itself to prevent further issues and ensure system stability.
     *
     * <p>
     * This method should be overridden by implementing classes to provide the necessary logic for starting the
     * component.
     * It is recommended to document any specific behavior or requirements of the component during the start phase.
     *
     * <p>
     * The default implementation of this method returns {@code true}.
     * Implementing classes should override this method and provide the necessary logic based on their requirements.
     *
     * <p>
     * Implementing classes should ensure proper error handling and provide meaningful error messages or logging if
     * the start phase fails.
     * Additionally, any necessary cleanup or rollback operations should be performed if applicable.
     *
     * <p>
     * This method should be invoked by the system during the server startup process. It should not be called manually.
     * The order of execution of the start phase among different components may vary depending on the specific system
     * configuration and requirements.
     *
     * @return {@code true} if the start phase was successful and the component started without errors.
     * {@code false} if the start phase encountered failures or errors that require the plugin to disable
     * itself.
     * @see #preStart()
     * @see #postStart()
     */
    default boolean start() {
        return true;
    }

    /**
     * The {@code postStart()} method is called during the server startup process after the component has been started.
     * It is invoked once the {@link #start()} method has completed successfully.
     *
     * <p>
     * Upon invoking the {@code postStart()} method, the component can perform any additional setup or initialization
     * steps required after the start phase.
     * This may include data initialization, or registering additional components or services.
     * The post-start method should return a boolean value indicating the success or failure of the post-start phase.
     * If any critical errors or failures occur during the post-start phase, the method should return {@code false}.
     * In such cases, the plugin may choose to disable itself to prevent further issues and ensure system stability.
     *
     * <p>
     * This method should be overridden by implementing classes to provide the necessary logic for the post-start phase.
     * It is recommended to document any specific behavior or requirements of the component during the post-start phase.
     *
     * <p>
     * The default implementation of this method returns {@code true}.
     * Implementing classes should override this method and provide the necessary logic based on their requirements.
     *
     * <p>
     * Implementing classes should ensure proper error handling and provide meaningful error messages or logging if
     * the post-start phase fails.
     * Additionally, any necessary cleanup or rollback operations should be performed if applicable.
     *
     * <p>
     * This method should be invoked by the system during the server startup process. It should not be called manually.
     * The order of execution of the post-start phase among different components may vary depending on the specific
     * system configuration and requirements.
     *
     * @return {@code true} if the post-start phase was successful and any additional setup completed without errors.
     * {@code false} if the post-start phase encountered failures or errors that require the plugin to
     * disable itself.
     * @see #preStart()
     * @see #start()
     */
    default boolean postStart() {
        return true;
    }

    /**
     * The {@code stop()} method is called during the server shutdown process to gracefully stop the component.
     * It is invoked when the server is shutting down or when the plugin is being disabled.
     *
     * <p>
     * Upon invoking the {@code stop()} method, the component should perform any necessary cleanup or shutdown
     * operations.
     * This may include closing connections, saving data, releasing resources, or unregistering listeners.
     * The stop method should handle any exceptions that occur during the shutdown process to ensure a graceful
     * shutdown.
     *
     * <p>
     * This method should be overridden by implementing classes to provide the necessary logic for stopping the
     * component.
     * It is recommended to document any specific behavior or requirements of the component during the stop phase.
     *
     * <p>
     * Implementing classes should ensure proper error handling and provide meaningful error messages or logging if
     * the stop phase encounters any issues.
     * Additionally, any necessary cleanup or finalization operations should be performed to leave the component in a
     * stable state.
     *
     * <p>
     * This method should be invoked by the system during the server shutdown process. It should not be called manually.
     * The order of execution of the stop phase among different components may vary depending on the specific system
     * configuration and requirements.
     *
     * @see #start()
     * @see #postStart()
     */
    default void stop() {
        // Implement the necessary logic for stopping the component
        // Perform cleanup, release resources, and handle any exceptions gracefully
    }

    default boolean prepareReload() {
        return true;
    }
}
