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

package net.kissenpvp.core.message;

import net.kissenpvp.core.api.config.ConfigurationImplementation;
import net.kissenpvp.core.api.message.Theme;
import net.kissenpvp.core.api.message.ThemeProvider;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.message.settings.*;
import net.kyori.adventure.text.BuildableComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public class DefaultTheme implements Theme
{

    @Override
    public @NotNull TextColor getPrimaryAccentColor()
    {
        return KissenCore.getInstance().getImplementation(ConfigurationImplementation.class).getSetting(DefaultPrimaryColor.class);
    }

    @Override
    public @NotNull TextColor getSecondaryAccentColor()
    {
        return KissenCore.getInstance().getImplementation(ConfigurationImplementation.class).getSetting(DefaultSecondaryColor.class);
    }

    @Override
    public @NotNull TextColor getDefaultColor()
    {
        return KissenCore.getInstance().getImplementation(ConfigurationImplementation.class).getSetting(DefaultColor.class);
    }

    @Override
    public @NotNull TextColor getEnabledColor()
    {
        return KissenCore.getInstance().getImplementation(ConfigurationImplementation.class).getSetting(DefaultEnabledColor.class);
    }

    @Override
    public @NotNull TextColor getDisabledColor()
    {
        return KissenCore.getInstance().getImplementation(ConfigurationImplementation.class).getSetting(DefaultDisabledColor.class);
    }

    /**
     * Replaces the colors of the provided components with personalized colors based on their current color values.
     * This method converts each component's color by mapping it to a personalized color obtained from the
     * ColorProviderImplementation.
     *
     * @param component the components to replace colors for.
     * @return a new Component with the replaced colors.
     * @throws NullPointerException if the component array or any of its elements are null.
     */
    @Override public @NotNull Component style(@NotNull Component... component)
    {
        return Component.join(JoinConfiguration.noSeparators(), Arrays.stream(component).map(this::transformComponent).toList());
    }
    /**
     * Converts a Component by replacing its color with a personalized color based on the current color value.
     * This method retrieves the personalized color by querying the ColorProviderImplementation.
     *
     * @param component the Component to convert.
     * @return the converted Component with the personalized color.
     * @throws NullPointerException if the component is null.
     */
    private @NotNull Component transformComponent(@NotNull Component component) {
        return transformComponent(component, getDefaultColor());
    }


    private @NotNull Component transformComponent(@NotNull Component component, @NotNull TextColor fallBack)
    {
        return Component.empty().append(component).toBuilder().mapChildrenDeep(buildableComponent -> transformSpecifiedComponent(buildableComponent, fallBack)).asComponent();
    }


    /**
     * Transforms a specified {@link BuildableComponent} by replacing its color with a personalized color based on
     * the current color value.
     *
     * @param buildableComponent the {@link BuildableComponent} to transform.
     * @return the transformed {@link BuildableComponent} with the personalized color.
     * @throws NullPointerException if the buildableComponent is null.
     */
    private @NotNull BuildableComponent<?, ?> transformSpecifiedComponent(@NotNull BuildableComponent<?, ?> buildableComponent, @NotNull TextColor fallBack)
    {
        if (buildableComponent instanceof TranslatableComponent translatableComponent) {

            return transformTranslatableComponent(translatableComponent, fallBack);
        }

        buildableComponent = legacyColorCodeResolver(buildableComponent);
        TextColor textColor = buildableComponent.color();
        if (textColor != null)
        {
            return (BuildableComponent<?, ?>) buildableComponent.color(getPersonalColorByCode(textColor.value()));
        }

        return (BuildableComponent<?, ?>) buildableComponent.color(fallBack);
    }

    @NotNull
    private TranslatableComponent transformTranslatableComponent(@NotNull TranslatableComponent translatableComponent, @NotNull TextColor fallBack) {
        Function<Component, Component> argumentMapper = argument -> transformComponent(argument, getPrimaryAccentColor());
        List<Component> transformedArgs = translatableComponent.args().stream().map(argumentMapper).toList();
        return translatableComponent.color(Objects.requireNonNullElse(translatableComponent.color(), fallBack)).args(transformedArgs.toArray(new Component[0]));
    }

    @NotNull private BuildableComponent<?, ?> legacyColorCodeResolver(@NotNull BuildableComponent<?, ?> buildableComponent)
    {
        Map<String, TextColor> replacements = new HashMap<>();
        String legacy = LegacyComponentSerializer.legacyAmpersand().serialize(buildableComponent);
        for (String textPassage : legacy.split("§"))
        {
            if (textPassage.length() > 1)
            {
                if (legacy.startsWith(textPassage) && !textPassage.startsWith("§")) {
                    continue;
                }
                switch (textPassage.substring(0, 1).toLowerCase()) {
                    case "p" -> replacements.put(textPassage.substring(1), getPrimaryAccentColor());
                    case "s" -> replacements.put(textPassage.substring(1), getSecondaryAccentColor());
                    case "t" -> replacements.put(textPassage.substring(1), getDefaultColor());
                    case "+" -> replacements.put(textPassage.substring(1), getEnabledColor());
                    case "-" -> replacements.put(textPassage.substring(1), getDisabledColor());
                }
            }
        }

        for (Map.Entry<String, TextColor> stringTextColorEntry : replacements.entrySet())
        {
            buildableComponent = (BuildableComponent<?, ?>) buildableComponent.replaceText(config ->
            {
                config.matchLiteral(stringTextColorEntry.getKey());
                config.replacement(Component.text(stringTextColorEntry.getKey()).color(stringTextColorEntry.getValue()));
            });
        }

        return buildableComponent;
    }

    /**
     * Retrieves a personalized TextColor based on the given color value.
     * This method queries the ColorProviderImplementation to obtain the personalized TextColor.
     *
     * @param value the color value to retrieve the personalized TextColor for.
     * @return the personalized TextColor.
     * @throws NullPointerException if the ColorProviderImplementation is null.
     */
    private @NotNull TextColor getPersonalColorByCode(int value)
    {
        return ThemeProvider.getColor((theme) -> theme.value() == value).orElse(TextColor.color(value));
    }
}
