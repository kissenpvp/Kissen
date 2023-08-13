package net.kissenpvp.core.ban;

import net.kissenpvp.core.message.KissenComponentSerializer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record BanOperatorNode(@Nullable UUID uuid, @NotNull String name)
{

    public BanOperatorNode(@Nullable UUID uuid, @NotNull Component name) {
        this(uuid, KissenComponentSerializer.getInstance().getJsonSerializer().serialize(name));
    }

    public @NotNull Component displayName() {
        return KissenComponentSerializer.getInstance().getJsonSerializer().deserialize(name());
    }
}
