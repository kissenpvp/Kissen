package net.kissenpvp.core.ban.warn;

import net.kissenpvp.core.api.time.TemporalData;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record WarnNode(int id, @NotNull String by, @Nullable Component reason, @NotNull TemporalData temporalMeasure) {}
