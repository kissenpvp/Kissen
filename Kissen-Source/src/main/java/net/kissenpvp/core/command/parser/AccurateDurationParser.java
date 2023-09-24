package net.kissenpvp.core.command.parser;

import net.kissenpvp.core.api.command.ArgumentParser;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.api.time.AccurateDuration;
import net.kissenpvp.core.api.time.TimeImplementation;
import net.kissenpvp.core.base.KissenCore;
import org.jetbrains.annotations.NotNull;

public class AccurateDurationParser<S extends ServerEntity> implements ArgumentParser<AccurateDuration, S> {
    @Override
    public @NotNull String serialize(@NotNull AccurateDuration object) {
        return object.getMillis() + "";
    }

    @Override
    public @NotNull AccurateDuration deserialize(@NotNull String input) {
        return KissenCore.getInstance().getImplementation(TimeImplementation.class).parse(input);
    }
}
