package net.kissenpvp.core.time;

import lombok.AccessLevel;
import lombok.Getter;
import net.kissenpvp.core.api.time.AccurateDuration;
import net.kissenpvp.core.api.time.TemporalObject;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public abstract class KissenTemporalObject implements TemporalObject {

    @Getter(AccessLevel.PROTECTED) private final TemporalMeasureNode temporalMeasureNode;

    protected KissenTemporalObject(TemporalMeasureNode temporalMeasureNode) {
        this.temporalMeasureNode = temporalMeasureNode;
    }

    @Override
    public @NotNull Instant getStart() {
        return Instant.ofEpochMilli(getTemporalMeasureNode().start());
    }

    @Override
    public @NotNull Optional<AccurateDuration> getAccurateDuration() {
        return Optional.ofNullable(getTemporalMeasureNode().duration().getValue()).map(KissenAccurateDuration::new);
    }

    @Override
    public @NotNull Optional<Instant> getEnd() {
        return Optional.ofNullable(getTemporalMeasureNode().end().getValue()).map(Instant::ofEpochMilli);
    }

    protected void rewriteEnd(@Nullable Instant end)
    {
        getTemporalMeasureNode().end().setValue(end == null ? null : end.toEpochMilli());
    }

    @Override
    public @NotNull Optional<Instant> getPredictedEnd() {
        return Optional.ofNullable(getTemporalMeasureNode().predictedEnd()).map(Instant::ofEpochMilli);
    }

    @Override
    public boolean isValid() {
        return getEnd().map(end -> end.isAfter(Instant.now())).orElse(true);
    }

    @Override
    public @NotNull Component endComponent(@NotNull DateTimeFormatter formatter)
    {
        return getEnd().map(end -> (Component) Component.text(formatter.format(end))).orElse(
                Component.translatable("server.ban.punishment.end.never"));
    }
}
