package net.kissenpvp.core.event;

import net.kissenpvp.core.api.base.Implementation;
import net.kissenpvp.core.api.event.EventClass;
import org.jetbrains.annotations.NotNull;

public interface EventImplementation extends Implementation {

    boolean call(@NotNull EventClass eventClass);

}
