package net.kissenpvp.core.user.rank.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.kissenpvp.core.api.event.Cancellable;
import net.kissenpvp.core.api.user.rank.AbstractPlayerRank;
import net.kissenpvp.core.api.user.rank.event.AbstractPlayerRankEvent;
import net.kissenpvp.core.api.user.rank.event.UndefinedAbstractRankEvent;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor @Getter @Setter
public class InternalPlayerRankEvent<T extends AbstractPlayerRank<?>> implements AbstractPlayerRankEvent<T>, Cancellable {

    private final T playerRank;
    private boolean cancelled;
}
