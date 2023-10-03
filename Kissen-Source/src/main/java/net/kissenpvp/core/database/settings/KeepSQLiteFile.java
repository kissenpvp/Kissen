package net.kissenpvp.core.database.settings;

import net.kissenpvp.core.api.config.options.OptionBoolean;
import org.jetbrains.annotations.NotNull;

public class KeepSQLiteFile extends OptionBoolean {
    @Override
    public @NotNull String getGroup() {
        return "database";
    }

    @Override
    public @NotNull String getDescription() {
        return "When you switch to a different database from SQLite, this feature will automatically delete the SQLite files for you.";
    }

    @Override
    public @NotNull Boolean getDefault() {
        return true;
    }
}
