package net.kissenpvp.core.api.base.serializer;

import org.jetbrains.annotations.NotNull;

public interface BaseSerializer<D, S> {

    @NotNull S serialize(@NotNull D object);

    @NotNull D deserialize(@NotNull S input);

}
