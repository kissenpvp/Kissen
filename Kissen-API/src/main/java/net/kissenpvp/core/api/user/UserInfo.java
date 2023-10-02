package net.kissenpvp.core.api.user;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface UserInfo {

    @NotNull UUID getUUID();

    @NotNull String getName();

}
