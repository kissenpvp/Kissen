package net.kissenpvp.core.api.networking.client.entitiy;

import net.kissenpvp.core.api.message.Theme;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

public interface MessageReceiver {

    @NotNull Theme getTheme();
}
