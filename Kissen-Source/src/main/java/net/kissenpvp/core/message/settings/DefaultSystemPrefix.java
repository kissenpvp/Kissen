package net.kissenpvp.core.message.settings;

import net.kissenpvp.core.api.config.options.OptionString;
import org.jetbrains.annotations.NotNull;

public class DefaultSystemPrefix extends OptionString
{
    @Override
    public @NotNull String getGroup()
    {
        return "appearance";
    }

    @Override
    public @NotNull String getDescription()
    {
        return "The default prefix placed in front of system messages.";
    }

    @Override
    public @NotNull String getDefault()
    {
        return "KissenPvP";
    }

    @Override
    public int getPriority()
    {
        return 1;
    }
}
