package net.kissenpvp.core.database.file;

import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.database.meta.ObjectMeta;
import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.api.database.savable.SavableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.Optional;

public class KissenObjectFileMeta extends KissenFileMeta implements ObjectMeta {

    //TODO
    public KissenObjectFileMeta(@NotNull String file) {
        super(file);
    }

    @Override
    public void add(@NotNull String id, @NotNull Map<String, String> data) throws BackendException {

    }

    @Override
    public @NotNull Optional<SavableMap> getData(@NotNull String totalId) throws BackendException {
        return Optional.empty();
    }

    @Override
    public @Unmodifiable @NotNull <T extends Savable> Map<@NotNull String, @NotNull SavableMap> getData(@NotNull T savable) throws BackendException {
        return null;
    }
}
