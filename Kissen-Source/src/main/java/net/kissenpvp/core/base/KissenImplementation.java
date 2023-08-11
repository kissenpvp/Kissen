package net.kissenpvp.core.base;

import net.kissenpvp.core.api.base.Implementation;
import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * The interface for Kissen system implementations that handle the lifecycle and functionalities of Kissen plugins.
 * This interface extends the {@link Implementation} interface, which defines general operations for system implementations.
 * Implementations of this interface are responsible for managing the setup, loading, enabling, and disabling of Kissen plugins.
 * They also handle post-loading and post-disabling operations, ensuring smooth interaction and coordination with plugins.
 * <p>
 * Kissen system implementations must provide custom logic and actions specific to their environment to facilitate the proper functioning
 * and integration of Kissen plugins within the overall Kissen framework.
 * <p>
 * Implementing classes should override the necessary methods in this interface to tailor the plugin lifecycle management and system behavior
 * according to their needs and requirements. The methods defined in this interface, along with the inherited ones from {@link Implementation},
 * together provide a complete set of lifecycle hooks for Kissen plugins and allow the system implementation to coordinate with plugins effectively.
 */
public interface KissenImplementation extends Implementation {

    /**
     * Default method signature for loading necessary data and resources for system implementations of Kissen-plugins.
     * This method is intended to be overridden by classes implementing the {@link KissenImplementation} interface to provide
     * custom loading behavior specific to their system implementation of a {@link KissenPlugin}.
     * <p>
     * When implementing this method, system implementations can perform tasks such as loading configuration files,
     * initializing data structures, registering event listeners, and setting up other necessary components
     * to prepare the plugin system for operation within the Kissen framework.
     *
     * @param kissenPlugin The {@link KissenPlugin} instance representing the specific plugin that the system implementation is loading for.
     *                     Must not be null.
     * @throws NullPointerException If the provided {@code kissenPlugin} parameter is null.
     */
    default void load(@NotNull KissenPlugin kissenPlugin) {}

    /**
     * Default method signature for performing post-load operations after the plugin has been initialized.
     * This method is intended to be overridden by classes implementing the {@link KissenImplementation} interface to provide
     * custom logic and actions specific to their system implementation of a Kissen plugin.
     * <p>
     * After the plugin has been initialized and its necessary resources have been loaded, this method allows system implementations
     * to perform additional setup, configuration, or interaction tasks that require the plugin to be in a fully initialized state.
     *
     * @param kissenPlugin The {@link KissenPlugin} instance representing the specific plugin that the system implementation is post-loading for.
     *                     Must not be null.
     * @throws NullPointerException If the provided {@code kissenPlugin} parameter is null.
     */
    default void postLoad(@NotNull KissenPlugin kissenPlugin) {}

    /**
     * Default method signature for enabling the Kissen plugin after its initialization and post-loading stages.
     * This method is intended to be overridden by classes implementing the {@link KissenImplementation} interface to provide
     * custom logic and actions specific to their system implementation of a Kissen plugin when enabling the plugin's functionality.
     * <p>
     * After the plugin has been initialized and its necessary resources have been loaded and post-loaded, this method allows
     * system implementations to perform additional tasks to enable the plugin's functionality within the Kissen framework.
     * <p>
     * Enabling the plugin typically includes starting background tasks, scheduling periodic updates, initializing feature components,
     * and registering event listeners to handle player interactions or other dynamic actions during the plugin's active state.
     *
     * @param kissenPlugin The {@link KissenPlugin} instance representing the specific plugin that the system implementation is enabling.
     *                     Must not be null.
     * @throws NullPointerException If the provided {@code kissenPlugin} parameter is null.
     */
    default void enable(@NotNull KissenPlugin kissenPlugin) {}

    /**
     * Default method signature for performing post-enable operations after the Kissen plugin has been fully enabled.
     * This method is intended to be overridden by classes implementing the {@link KissenImplementation} interface to provide
     * custom logic and actions specific to their system implementation of a Kissen plugin.
     * <p>
     * After the plugin has been enabled, and all its functionality has been initialized and activated, this method allows
     * system implementations to perform additional tasks that require the plugin to be fully operational and active within the Kissen framework.
     * <p>
     * Post-enable operations might include executing tasks that depend on other plugins or services being fully loaded,
     * performing additional setup or configuration for complex features, or interacting with external systems.
     *
     * @param kissenPlugin The {@link KissenPlugin} instance representing the specific plugin that the system implementation is post-enabling.
     *                     Must not be null.
     * @throws NullPointerException If the provided {@code kissenPlugin} parameter is null.
     */
    default void postEnable(@NotNull KissenPlugin kissenPlugin) {}

    /**
     * Default method signature for disabling the Kissen plugin and performing cleanup tasks before it is fully unloaded.
     * This method is intended to be overridden by classes implementing the {@link KissenImplementation} interface to provide
     * custom logic and actions specific to their system implementation of a Kissen plugin when disabling the plugin's functionality.
     * <p>
     * When a plugin is being disabled, it should perform necessary cleanup tasks, save data, release resources, and stop any active tasks.
     * The method allows system implementations to gracefully shut down the plugin before it is fully unloaded from the Kissen framework.
     * <p>
     * Disabling the plugin typically includes stopping background tasks, saving configuration files, releasing resources, and performing
     * any final actions required to gracefully terminate the plugin's functionality.
     *
     * @param kissenPlugin The {@link KissenPlugin} instance representing the specific plugin that the system implementation is disabling.
     *                     Must not be null.
     * @throws NullPointerException If the provided {@code kissenPlugin} parameter is null.
     */
    default void disable(@NotNull KissenPlugin kissenPlugin) {}

    /**
     * Default method signature for performing post-disable operations after the Kissen plugin has been fully disabled and unloaded.
     * This method is intended to be overridden by classes implementing the {@link KissenImplementation} interface to provide
     * custom logic and actions specific to their system implementation of a Kissen plugin.
     * <p>
     * After the plugin has been fully disabled and unloaded from the Kissen framework, this method allows system implementations
     * to perform additional cleanup tasks or post-disable actions that require the plugin to be no longer active.
     * <p>
     * Post-disable operations might include performing final data storage, releasing resources, logging statistics, or notifying other components
     * about the plugin's shutdown state, among other tasks that need to be done after the plugin is no longer operational.
     *
     * @param kissenPlugin The {@link KissenPlugin} instance representing the specific plugin that the system implementation is post-disabling.
     *                     Must not be null.
     * @throws NullPointerException If the provided {@code kissenPlugin} parameter is null.
     */
    default void postDisable(@NotNull KissenPlugin kissenPlugin) {}

    /**
     * Default method signature for indicating that the setup process for all Kissen plugins has been completed and all plugins have been enabled.
     * This method is intended to be overridden by classes implementing the {@link KissenImplementation} interface to provide custom logic
     * and actions specific to their system implementation of Kissen plugins after the entire setup phase has finished.
     * <p>
     * When this method is called, it signals that all plugins have completed their initialization, post-loading, and enabling stages,
     * and the entire Kissen system is ready to function as intended.
     * <p>
     * System implementations can use this method to perform any additional setup, configuration, or coordination tasks that depend on
     * the successful setup of all plugins or to trigger actions that need to occur after the entire plugin ecosystem is active and operational.
     * <p>
     * Note: This method is called when all plugins have been initialized and enabled, and it is guaranteed that the setup process for all plugins is completed.
     * It should not be called or triggered manually by external code.
     */
    default void setupComplete() {}
}
