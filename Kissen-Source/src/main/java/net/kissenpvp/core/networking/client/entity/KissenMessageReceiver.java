package net.kissenpvp.core.networking.client.entity;

import net.kissenpvp.core.api.config.Option;
import net.kissenpvp.core.api.message.ChatImportance;
import net.kissenpvp.core.api.message.ComponentSerializer;
import net.kissenpvp.core.api.networking.client.entitiy.ConsoleClient;
import net.kissenpvp.core.api.networking.client.entitiy.MessageReceiver;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.config.KissenConfigurationImplementation;
import net.kissenpvp.core.message.DefaultTheme;
import net.kissenpvp.core.message.EnumColorProvider;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public interface KissenMessageReceiver extends MessageReceiver {

    @Override
    default void sendMessage(@NotNull String... text) {
        sendMessage(KissenCore.getInstance().getConsole(), text);
    }

    @Override
    default void sendMessage(@NotNull Component... component) {
        sendMessage(KissenCore.getInstance().getConsole(), component);
    }

    @Override
    default void sendMessage(@NotNull ChatImportance chatImportance, @NotNull String... text) {
        sendMessage(KissenCore.getInstance().getConsole(), chatImportance, text);
    }

    @Override
    default void sendMessage(@NotNull ChatImportance chatImportance, @NotNull Component... component) {
        sendMessage(KissenCore.getInstance().getConsole(), chatImportance, component);
    }

    @Override
    default void sendMessage(@NotNull ConsoleClient sender, @NotNull String... text) {
        sendMessage(sender, ChatImportance.NORMAL, text);
    }

    @Override
    default void sendMessage(@NotNull ConsoleClient sender, @NotNull Component... component) {
        sendMessage(sender, ChatImportance.NORMAL, component);
    }

    @Override
    default void sendMessage(@NotNull ConsoleClient sender, @NotNull ChatImportance chatImportance, @NotNull String... text) {
        sendMessage(sender, chatImportance, Component.join(JoinConfiguration.newlines(), Arrays.stream(text).map(string -> ComponentSerializer.getInstance().getLegacySerializer().deserialize(string)).collect(Collectors.toSet())));
    }

    @Override
    default void sendMessage(@NotNull ConsoleClient sender, @NotNull ChatImportance chatImportance, @NotNull Component... component) {
        Audience audience = getKyoriAudience();

        Component mappedComponents = Component.join(JoinConfiguration.noSeparators(), component);

        if (!(sender instanceof PlayerClient<?>)) {
            mappedComponents = replaceSettings(mappedComponents);
        }

        audience.sendMessage(styleMessage(mappedComponents).append(Component.empty().color(EnumColorProvider.DEFAULT.getTextColor())));
    }

    private @NotNull Component replaceSettings(@NotNull Component mappedComponents) {
        for (Set<Option<?>> optionList : KissenCore.getInstance().getImplementation(KissenConfigurationImplementation.class).getKissenPluginSetMap().values()) {
            for (Option<?> option : optionList) {
                mappedComponents = replaceSetting(mappedComponents, option);
            }
        }
        return mappedComponents;
    }

    private @NotNull Component replaceSetting(@NotNull Component mappedComponents, @NotNull Option<?> option) {
        mappedComponents = mappedComponents.replaceText(config ->
        {
            config.match("%" + option.getCode().toLowerCase() + "%");
            if (option.getValue() instanceof Component optionValue) {
                config.replacement(optionValue);
                return;
            }
            config.replacement(option.serialize());
        });
        return mappedComponents;
    }

    @Override
    default @NotNull Component styleMessage(Component... component) {
        return ((DefaultTheme) getTheme()).replaceColors(component);
    }
}
