package net.kissenpvp.core.user.rank.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.kissenpvp.core.api.event.Cancellable;
import net.kissenpvp.core.api.user.rank.AbstractRank;
import net.kissenpvp.core.api.user.rank.event.AbstractRankEvent;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor@Getter @Setter
public class InternalRankEvent<T extends AbstractRank> implements AbstractRankEvent<T>, Cancellable {

    private final T rankTemplate;
    private boolean cancelled;
}
