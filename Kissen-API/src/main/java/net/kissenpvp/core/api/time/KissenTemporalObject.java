package net.kissenpvp.core.api.time;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public abstract class KissenTemporalObject implements TemporalObject {

    private final TemporalData temporalData;

    protected KissenTemporalObject(TemporalData temporalData) {
        this.temporalData = temporalData;
    }

    public TemporalData getTemporalData() {
        return temporalData;
    }

    @Override
    public @NotNull Instant getStart() {
        return Instant.ofEpochMilli(getTemporalData().start());
    }

    @Override
    public @NotNull Optional<AccurateDuration> getAccurateDuration() {
        return Optional.ofNullable(getTemporalData().duration().getValue()).map(AccurateDuration::new);
    }

    @Override
    public @NotNull Optional<Instant> getEnd() {
        return Optional.ofNullable(getTemporalData().end().getValue()).map(Instant::ofEpochMilli);
    }

    @Override
    public @NotNull Optional<Instant> getPredictedEnd() {
        return Optional.ofNullable(getTemporalData().predictedEnd()).map(Instant::ofEpochMilli);
    }

    @Override
    public boolean isValid() {
        return getEnd().map(end -> end.isAfter(Instant.now())).orElse(true);
    }

    @Override
    public @NotNull Component endComponent(@NotNull DateTimeFormatter formatter)
    {
        return getEnd().map(end -> (Component) Component.text(formatter.format(end))).orElseGet(() -> {
            String translationKey = "server.ban.punishment.end.never";
            return Component.translatable(translationKey);
        });
    }

    protected void rewriteEnd(@Nullable Instant end) {
        getTemporalData().end().setValue(end==null ? null:end.toEpochMilli());
    }
}
