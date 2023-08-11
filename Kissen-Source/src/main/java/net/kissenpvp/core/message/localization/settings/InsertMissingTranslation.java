package net.kissenpvp.core.message.localization.settings;

import net.kissenpvp.core.api.config.options.OptionBoolean;
import org.jetbrains.annotations.NotNull;

public class InsertMissingTranslation extends OptionBoolean {
    @Override
    public @NotNull String getGroup() {
        return "localization";
    }

    @Override
    public @NotNull String getDescription() {
        return "Adds missing translations to files when they are not present.";
    }

    @Override
    public @NotNull Boolean getDefault() {
        return true;
    }
}
