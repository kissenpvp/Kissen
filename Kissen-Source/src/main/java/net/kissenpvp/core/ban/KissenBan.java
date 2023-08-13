package net.kissenpvp.core.ban;

import net.kissenpvp.core.api.ban.Ban;
import net.kissenpvp.core.api.ban.BanType;
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.database.savable.KissenSavable;
import net.kissenpvp.core.database.savable.SerializableSavableHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("RedundantThrows") //TODO events
public abstract class KissenBan extends KissenSavable implements Ban {

    @Override
    public int getID() {
        return Integer.parseInt(getRawID());
    }

    @Override
    public @NotNull String getSaveID() {
        return "banid";
    }

    @Override
    public String[] getKeys() {
        return new String[]{"name", "ban_type"};
    }

    @Override
    public @NotNull String getName() {
        return getNotNull("name");
    }

    @Override
    public void setName(@NotNull String name) throws EventCancelledException {
        set("name", name);
    }

    @Override
    public @NotNull BanType getBanType() {
        return BanType.valueOf(getNotNull("ban_type"));
    }

    @Override
    public void setBanType(@NotNull BanType banType) throws EventCancelledException {
        set("ban_type", banType.name());
    }

    @Override
    public @NotNull Optional<@Nullable Duration> getDuration() {
        return get("duration").map(val -> Duration.of(Long.parseLong(val), TimeUnit.MILLISECONDS.toChronoUnit()));
    }

    @Override
    public void setDuration(@Nullable Duration duration) throws EventCancelledException {
        set("ban_type", Optional.ofNullable(duration)
                .map(Duration::toMillis)
                .map(String::valueOf)
                .orElse(null));
    }

    @Override
    public SerializableSavableHandler getSerializableSavableHandler() {
        return null;
    }
}
