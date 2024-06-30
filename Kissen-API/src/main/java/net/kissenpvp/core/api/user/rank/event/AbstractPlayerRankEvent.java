package net.kissenpvp.core.api.user.rank.event;

import net.kissenpvp.core.api.user.rank.AbstractPlayerRank;
import org.jetbrains.annotations.NotNull;

public interface AbstractPlayerRankEvent<T extends AbstractPlayerRank<?>> extends UndefinedAbstractRankEvent<T> {

    @NotNull T getPlayerRank();

}
