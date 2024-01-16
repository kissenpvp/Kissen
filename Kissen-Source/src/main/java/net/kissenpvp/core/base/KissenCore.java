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

package net.kissenpvp.core.base;

import lombok.Getter;
import net.kissenpvp.core.api.base.Implementation;
import net.kissenpvp.core.api.base.ImplementationAbsentException;
import net.kissenpvp.core.api.base.Kissen;
import net.kissenpvp.core.api.base.loader.Ignore;
import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import net.kissenpvp.core.api.config.ConfigurationImplementation;
import net.kissenpvp.core.api.database.DataImplementation;
import net.kissenpvp.core.api.database.StorageImplementation;
import net.kissenpvp.core.api.database.connection.DatabaseImplementation;
import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.ObjectMeta;
import net.kissenpvp.core.api.event.EventImplementation;
import net.kissenpvp.core.api.message.ChatImplementation;
import net.kissenpvp.core.api.message.MessageImplementation;
import net.kissenpvp.core.api.networking.APIRequestImplementation;
import net.kissenpvp.core.api.reflection.ReflectionImplementation;
import net.kissenpvp.core.api.reflection.ReflectionPackage;
import net.kissenpvp.core.api.time.TimeImplementation;
import net.kissenpvp.core.api.util.PageImplementation;
import net.kissenpvp.core.config.KissenConfigurationImplementation;
import net.kissenpvp.core.database.KissenDataImplementation;
import net.kissenpvp.core.database.KissenDatabaseImplementation;
import net.kissenpvp.core.database.savable.KissenStorageImplementation;
import net.kissenpvp.core.event.KissenEventImplementation;
import net.kissenpvp.core.message.KissenChatImplementation;
import net.kissenpvp.core.message.KissenMessageImplementation;
import net.kissenpvp.core.networking.KissenAPIRequestImplementation;
import net.kissenpvp.core.permission.event.KissenPermissionGroupCreateEvent;
import net.kissenpvp.core.reflection.KissenReflectionClass;
import net.kissenpvp.core.reflection.KissenReflectionImplementation;
import net.kissenpvp.core.time.KissenTimeImplementation;
import net.kissenpvp.core.util.KissenPageImplementation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
public abstract class KissenCore implements Kissen {

    @Getter
    private static KissenCore instance;
    private final Logger logger;
    private Map<Class<? extends Implementation>, Implementation> implementation;
    private ObjectMeta publicMeta;

    protected KissenCore(@NotNull Logger logger) {
        this.logger = logger;
    }

    /**
     * Starts the Kissen system by loading plugin implementations, initializing serializers, and invoking necessary operations.
     * This method is called to initialize and bootstrap the Kissen system within the KissenPlugin framework.
     *
     * @param clazz The Class object representing the main class of the KissenPlugin. Must not be null.
     */
    public void initialize(@NotNull Class<?> clazz)
    {
        try
        {
            //Load plugin implementations
            long time = System.currentTimeMillis();
            KissenCore.instance = this;

            implementation = new HashMap<>();
            loadImplementations(implementation);

            getLogger().debug("The following implementations have been loaded: ");
            implementation.forEach((key, value) -> getLogger().debug("- {}", key.getName()));

            startImplementations();
            time = System.currentTimeMillis() - time;
            getLogger().info("The kissen system took {}ms to start.", time);
            getImplementation(EventImplementation.class).call(new KissenPermissionGroupCreateEvent("", null));
        }
        catch (IOException exception)
        {
            throw new BackendException(exception);
        }
    }

    protected void loadImplementations(@NotNull Map<Class<? extends Implementation>, Implementation> loader)
    {
        loader.put(APIRequestImplementation.class, new KissenAPIRequestImplementation());
        loader.put(ChatImplementation.class, new KissenChatImplementation());
        loader.put(ConfigurationImplementation.class, new KissenConfigurationImplementation());
        loader.put(DataImplementation.class, new KissenDataImplementation());
        loader.put(DatabaseImplementation.class, new KissenDatabaseImplementation());
        loader.put(EventImplementation.class, new KissenEventImplementation());
        loader.put(PageImplementation.class, new KissenPageImplementation());
        loader.put(TimeImplementation.class, new KissenTimeImplementation());
        loader.put(ReflectionImplementation.class, new KissenReflectionImplementation());
        loader.put(StorageImplementation.class, new KissenStorageImplementation());
        loader.put(MessageImplementation.class, new KissenMessageImplementation());
    }

    /**
     * Starts the Kissen system by triggering the onPluginFinish method for each KissenImplementation.
     * <p>
     * This method is called to finalize the setup and initialization of the Kissen system within the Kissen framework.
     * It invokes the {@link KissenImplementation#setupComplete()} method for each registered {@link KissenImplementation}, allowing them to perform any final setup steps.
     */
    public void start() {
        getKissenImplementations().forEach(KissenImplementation::setupComplete);
    }

    /**
     * Shuts down the Kissen system by stopping all registered implementations.
     * <p>
     * This method is called to gracefully terminate the KissenPlugin framework and unload all plugin implementations.
     * It iterates through the registered implementations and calls the stop method for each implementation,
     * allowing them to perform any necessary cleanup tasks before the plugin is fully shut down.
     * Any exceptions that occur during the shutdown process are logged using the plugin's logger.
     */
    public void shutdown() {
        getLogger().debug("## STOP ##");

        getLogger().debug("Shutting down implementations...");
        implementation.forEach((key, value) -> {
            try {
                value.stop();
            } catch (Exception exception) {
                getLogger().error("An error occurred when shutting down implementation '" + key.getSimpleName() + "'",
                        exception);
            }
        });
    }

    private void startImplementations() throws IOException {
        getLogger().debug("Scan for class scanner entries.");

        File file = new File("kissen.properties");
        getImplementation(KissenConfigurationImplementation.class).loadInternalConfiguration(file);
        getLogger().info("Configuration loaded from '{}'. Any missing values have been written to the file.",
                file.getAbsolutePath());

        getLogger().debug("Enable locale system and load Languages.");

        publicMeta = getImplementation(KissenDatabaseImplementation.class).connectDatabase().createObjectMeta(
                "kissen_public_meta");

        getLogger().debug("## Start ##");

        runOperation(OperationState.PRE);

        getLogger().debug("Enable class scanner entries");

        runOperation(OperationState.START);
        runOperation(OperationState.POST);
    }

    public <T extends Implementation> @NotNull T getImplementation(@NotNull Class<T> implementation) {
        Object obj = this.implementation.get(implementation);
        if (obj != null) {
            return implementation.cast(obj);
        } else {
            for (Object val : this.implementation.values()) {
                if (implementation.isAssignableFrom(val.getClass())) {
                    return implementation.cast(val);
                }
            }
        }
        throw new ImplementationAbsentException();
    }

    private void runOperation(@NotNull OperationState operationState) {

        getLogger().debug("Run {} operation state...", operationState.name().toLowerCase(Locale.ENGLISH));

        Set<Implementation> alreadyExecuted = new HashSet<>(); //prevent double execution
        for (Implementation implementation : implementation.values()) {
            if (alreadyExecuted.contains(implementation)) {
                continue;
            }
            try {
                switch (operationState) {
                    case PRE -> implementation.preStart();
                    case START -> implementation.start();
                    case POST -> implementation.postStart();
                }
                alreadyExecuted.add(implementation);
            } catch (Exception exception) {
                throw new IllegalStateException(String.format("An error occurred when running %s on implementation %s",
                        operationState.name().toLowerCase(Locale.ENGLISH), implementation.getClass().getName()),
                        exception);
            }
        }
    }

    private @Unmodifiable @NotNull Set<Class<?>> loadClasses(@NotNull ReflectionPackage... reflectionPackage) {
        Set<Class<?>> classes = new HashSet<>();
        Arrays.stream(reflectionPackage).map(reflectionPackage1 -> reflectionPackage1.getClasses().stream().filter(
                clazz -> !clazz.isAnnotationPresent(Ignore.class)).collect(Collectors.toSet())).forEach(
                classes::addAll);
        return classes;
    }

    public @NotNull @Unmodifiable Set<KissenImplementation> getKissenImplementations() {
        return KissenCore.getInstance().getImplementation().values().stream().filter(
                implementation -> implementation instanceof KissenImplementation).map(
                implementation1 -> (KissenImplementation) implementation1).collect(Collectors.toSet());
    }

    private void injectImplementations(@NotNull Set<Class<?>> reflectionClasses) {
        Set<Class<?>> allFiles = reflectionClasses.stream().filter(Implementation.class::isAssignableFrom).collect(
                Collectors.toUnmodifiableSet());

        Set<Class<?>> implementationFiles = allFiles.stream().filter(
                clazz -> Modifier.isInterface(clazz.getModifiers())).filter(
                clazz -> !clazz.equals(Implementation.class) && !clazz.equals(KissenImplementation.class)).collect(
                Collectors.toSet());

        Set<Class<?>> sourceFiles = allFiles.stream().filter(
                clazz -> !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())).filter(
                clazz -> allFiles.stream().noneMatch(
                        intern -> clazz != intern && clazz.isAssignableFrom(intern))).collect(Collectors.toSet());

        sourceFiles.forEach(sourceClazz -> {
            Implementation implementationInstance = (Implementation) new KissenReflectionClass(
                    sourceClazz).newInstance();
            implementationFiles.stream().filter(
                    clazz -> clazz.isAssignableFrom(sourceClazz)).findFirst().ifPresentOrElse(
                    clazz -> implementation.put((Class<? extends Implementation>) clazz, implementationInstance),
                    () -> implementation.put((Class<? extends Implementation>) sourceClazz, implementationInstance));
        });
    }

    public void load(@NotNull PluginState pluginState, @NotNull KissenPlugin kissenPlugin) {
        switch (pluginState) {
            case PRE -> iterateKissenImplementation(implementation -> implementation.load(kissenPlugin));
            case POST -> iterateKissenImplementation(implementation -> implementation.postLoad(kissenPlugin));
        }
    }

    public void enable(@NotNull PluginState pluginState, @NotNull KissenPlugin kissenPlugin) {
        switch (pluginState) {
            case PRE -> iterateKissenImplementation(implementation -> implementation.enable(kissenPlugin));
            case POST -> iterateKissenImplementation(implementation -> implementation.postEnable(kissenPlugin));
        }
    }

    public void disable(@NotNull PluginState pluginState, @NotNull KissenPlugin kissenPlugin) {
        switch (pluginState) {
            case PRE -> iterateKissenImplementation(implementation -> implementation.disable(kissenPlugin));
            case POST -> iterateKissenImplementation(implementation -> implementation.postDisable(kissenPlugin));
        }
    }

    private void iterateKissenImplementation(@NotNull Consumer<KissenImplementation> kissenImplementationConsumer) {
        getKissenImplementations().forEach(kissenImplementationConsumer);
    }

    public abstract void runTask(@NotNull KissenPlugin kissenPlugin, @NotNull Runnable runnable);

    public abstract void runTask(@NotNull Runnable runnable, int delay, @NotNull String name);

    private enum OperationState {
        PRE, START, POST
    }
}
