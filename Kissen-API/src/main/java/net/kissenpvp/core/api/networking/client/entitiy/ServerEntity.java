package net.kissenpvp.core.api.networking.client.entitiy;

import net.kissenpvp.core.api.ban.BanOperator;
import net.kissenpvp.core.api.networking.client.Client;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public interface ServerEntity extends Client, BanOperator {

    @NotNull String getName();

    @NotNull Locale getCurrentLocale();
}
