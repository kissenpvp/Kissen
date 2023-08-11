package net.kissenpvp.core.networking.client.entity;

import net.kissenpvp.core.api.message.Theme;
import net.kissenpvp.core.api.message.localization.LocalizationImplementation;
import net.kissenpvp.core.api.networking.client.entitiy.ConsoleClient;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.message.DefaultTheme;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public interface KissenConsoleClient extends ConsoleClient, KissenServerEntity, KissenMessageReceiver {

    @Override
    default boolean isConnected() {
        return true;
    }

    @Override
    default @NotNull Theme getTheme() {
        return new DefaultTheme();
    }

    @Override
    default @NotNull Locale getCurrentLocale() {
        return KissenCore.getInstance().getImplementation(LocalizationImplementation.class).getDefaultLocale();
    }
}
