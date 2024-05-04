package net.kissenpvp.core.ban.events.ban;

import lombok.Setter;
import net.kissenpvp.core.api.ban.AbstractBan;
import net.kissenpvp.core.api.time.AccurateDuration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Setter
public class BanAlterDurationEvent<B extends AbstractBan> extends BanEvent<B> {

    private AccurateDuration accurateDuration;

    public BanAlterDurationEvent(@NotNull B ban, @Nullable AccurateDuration accurateDuration) {
        super(ban);
        this.accurateDuration = accurateDuration;
    }

    public Optional<AccurateDuration> getAccurateDuration() {
        return Optional.ofNullable(accurateDuration);
    }
}
