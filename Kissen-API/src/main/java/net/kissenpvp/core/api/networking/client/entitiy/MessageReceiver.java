package net.kissenpvp.core.api.networking.client.entitiy;

import net.kissenpvp.core.api.message.ChatImportance;
import net.kissenpvp.core.api.message.Theme;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface MessageReceiver {
    void sendMessage(@NotNull String... text);

    void sendMessage(@NotNull Component... component);

    void sendMessage(@NotNull ChatImportance chatImportance, @NotNull String... text);

    void sendMessage(@NotNull ChatImportance chatImportance, @NotNull Component... component);

    void sendMessage(@NotNull ConsoleClient sender, @NotNull String... text);

    void sendMessage(@NotNull ConsoleClient sender, @NotNull Component... component);

    void sendMessage(@NotNull ConsoleClient sender, @NotNull ChatImportance chatImportance, @NotNull String... text);

    void sendMessage(@NotNull ConsoleClient sender, @NotNull ChatImportance chatImportance, @NotNull Component... component);

    @NotNull
    Component styleMessage(Component... component);

    @NotNull Theme getTheme();

    @NotNull
    Audience getKyoriAudience();
}
