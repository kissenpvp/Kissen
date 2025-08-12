package net.kissenpvp.api.temporal;

import org.jetbrains.annotations.NotNull;

public interface WritableTemporalSubscriber extends TemporalSubscriber {

    @Override
    @NotNull WritableTemporalObject temporal();
}
