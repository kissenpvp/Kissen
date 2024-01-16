package net.kissenpvp.core.ban.warn;

import net.kissenpvp.core.time.TemporalMeasureNode;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public record WarnNode(int id, @NotNull String by, @Override String reason, @NotNull TemporalMeasureNode temporalMeasureNode) {}
