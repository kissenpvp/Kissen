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

package net.kissenpvp.core.api.message.localization;

import net.kissenpvp.core.api.base.Implementation;
import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Set;

/**
 * The interface for Localization implementations within the Kissen framework.
 * This interface extends the {@link Implementation} interface, which defines general operations for system implementations.
 * Implementations of this interface are responsible for handling localization and translation functionalities for Kissen plugins.
 * They provide methods to manage available locales, register translation keys, get default locales, and perform translations for messages.
 * <p>
 * Localization implementations allow Kissen plugins to provide translated messages based on the user's preferred language or locale,
 * enabling multi-language support within the plugin ecosystem.
 * <p>
 * Implementing classes should override the methods in this interface to provide custom localization behavior specific to their system
 * and the requirements of Kissen plugins.
 */
public interface LocalizationImplementation extends Implementation {

    /**
     * Retrieves the set of available locales for a specific {@link KissenPlugin}.
     * <p>
     * This method is used to query for the set of locales that are available and supported
     * by the specified plugin. A {@link Locale} represents a specific language and country combination, and it is used for
     * translating messages and providing localized content to users based on their preferences.
     *
     * @param kissenPlugin The {@link KissenPlugin} instance representing the plugin to query for available locales. Must not be {@code null}.
     * @return An unmodifiable set of {@link Locale} objects representing the available locales for the specified plugin.
     * The {@link Set} contains all the supported locales that the plugin can use for providing translated messages and content.
     * If no specific locales are supported, an empty set will be returned.
     * @throws NullPointerException If the provided {@link KissenPlugin} parameter is null.
     */
    @Unmodifiable @NotNull Set<Locale> getAvailableLocales(@NotNull KissenPlugin kissenPlugin);

    /**
     * Registers a translation key with its default message for a specific {@link KissenPlugin}.
     * <p>
     * This method is used to register a translation key along with its default message in the localization system of the framework.
     * Translation keys are used as identifiers to retrieve translated messages for different locales, allowing plugins to provide multi-language support.
     * <p>
     * When a translation key is registered, it becomes accessible in the localization system, and plugins can use it to provide localized messages
     * based on the user's preferred language or locale. These can be either accessed using {@link Component#translatable(String)}
     *
     * @param kissenPlugin   The {@link KissenPlugin} instance representing the plugin where the translation key will be registered. Must not be null.
     * @param key            The string key used for translation. It serves as the identifier for the translated message and must be unique within the plugin. Must not be null.
     * @param defaultMessage The default message (in {@link MessageFormat}) to use when no translation is available for the key. This default message will be used if the translation for the key and the user's locale are not found. Must not be null.
     * @throws NullPointerException If any of the provided parameters ({@code kissenPlugin}, {@code key}, or {@code defaultMessage}) is null.
     */
    void register(@NotNull KissenPlugin kissenPlugin, @NotNull String key, @NotNull MessageFormat defaultMessage);

    /**
     * Retrieves the default locale for the server.
     * <p>
     * This method is used to obtain the default {@link Locale} used throughout the server for translation purposes.
     * The default locale represents the fallback locale that the system uses when a specific translation for a locale is not available.
     * It serves as the base locale for providing translated messages and content when a user's preferred locale is not supported or available.
     *
     * @return The default {@link Locale} used by the server for translation purposes.
     */
    @NotNull Locale getDefaultLocale();

    /**
     * Retrieves the {@link Locale} object for a given locale tag.
     * <p>
     * This method is used to obtain the corresponding {@link Locale} object based on a provided locale tag.
     * A locale tag is a string representation of a specific language and country combination, typically in the form of language tags, such as "en_US" for English (United States).
     * The locale tag is used to identify the desired locale for translating messages and providing localized content to users.
     * <p>
     * Note that the {@link Locale} will get built if it is not yet registered.
     *
     * @param localeTag The locale tag as a string, representing the desired {@link Locale}. Must not be null.
     * @return The {@link Locale} object corresponding to the provided locale tag.
     * @throws NullPointerException If the provided {@code localeTag} parameter is null.
     */
    @NotNull Locale getLocale(@NotNull String localeTag);

    /**
     * Retrieves the name of a {@link Locale} as a {@link Component}.
     * <p>
     * This method is used to obtain the localized name of a given {@link Locale} as a {@link Component}.
     * The localized name represents the display name of the locale, typically in the language of the locale itself.
     * It is used to present user-friendly locale names in the user interface or settings, enabling users to select their preferred language.
     *
     * @deprecated use {@link Locale#getDisplayName()} when loading the locale with {@link #getLocale(String)}. This method will be removed in 1.21
     * @param locale The {@link Locale} for which the name will be retrieved. Must not be null.
     * @return The name of the provided {@link Locale} as a {@link Component}.
     * @throws NullPointerException If the provided {@code locale} parameter is null.
     */
    @Deprecated(since = "1.20.6", forRemoval = true)
    @NotNull Component getLocaleName(@NotNull Locale locale);

    /**
     * Translates a message for a specific locale based on the given translation key and variables.
     * <p>
     * This method is used to retrieve a translated {@link Component} message for a specific {@link Locale} and translation key.
     * The translation key serves as the identifier for the desired message, while the locale determines the language and country for the translation.
     * Additionally, the method allows for variable placeholders within the translated message, which can be replaced with specific values provided in the 'var' array.
     *
     * @deprecated Use {@code Component#translatable}. Will get removed in 1.21.
     * @param kissenPlugin The {@link KissenPlugin} instance representing the plugin from which the translation is requested. Must not be null.
     * @param key          The string key used for translation. It identifies the message to be translated. Must not be null.
     * @param locale       The {@link Locale} for which the message will be translated. Must not be null.
     * @param var          An array of strings representing the variables to replace placeholders in the translated message.
     *                     These variables are used to dynamically insert values into the translated message, based on the placeholders present in the message.
     *                     The 'var' array can be empty if the translated message does not contain any placeholders.
     * @return The translated {@link Component} message for the specified locale and translation key, with variables replaced by the provided 'var' values.
     * If no translation is available for the key and the locale, the method may return the default message as obtained from the {@link #register} method.
     * @throws NullPointerException If any of the provided parameters ({@code kissenPlugin}, {@code key}, {@code locale}, or {@code var}) is null.
     */
    @Deprecated(since = "1.20.6", forRemoval = true)
    @NotNull Component translate(@NotNull KissenPlugin kissenPlugin, @NotNull String key, @NotNull Locale locale, @NotNull String... var);

    /**
     * Translates a {@link TranslatableComponent} for a specific locale.
     * <p>
     * This method is used to retrieve a translated {@link Component} message for a specific {@link Locale} based on a {@link TranslatableComponent}.
     * A {@link TranslatableComponent} is a specialized component that holds a translation key along with optional variables for dynamic content.
     * The translation key serves as the identifier for the desired message, while the locale determines the language and country for the translation.
     *
     * @deprecated Use {@code Component#translatable}. Will get removed in 1.21.
     * @param kissenPlugin          The {@link KissenPlugin} instance representing the plugin from which the translation is requested. Must not be null.
     * @param translatableComponent The {@link TranslatableComponent} to be translated. It contains the translation key and optional variables for dynamic content. Must not be null.
     * @param locale                The {@link Locale} for which the message will be translated. Must not be null.
     * @return The translated {@link Component} message for the specified locale and {@link TranslatableComponent}.
     * If no translation is available for the key and the locale, the method may return the default message as obtained from the {@link #register} method.
     * @throws NullPointerException If any of the provided parameters ({@code kissenPlugin}, {@code translatableComponent}, or {@code locale}) is null.
     */
    @Deprecated(since = "1.20.6", forRemoval = true)
    @NotNull Component translate(@NotNull KissenPlugin kissenPlugin, @NotNull TranslatableComponent translatableComponent, @NotNull Locale locale);
}
