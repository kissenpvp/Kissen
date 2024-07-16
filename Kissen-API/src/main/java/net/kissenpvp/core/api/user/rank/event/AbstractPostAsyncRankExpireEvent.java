package net.kissenpvp.core.api.user.rank.event;

import net.kissenpvp.core.api.user.rank.AbstractPlayerRank;
import org.jetbrains.annotations.NotNull;

public interface AbstractPostAsyncRankExpireEvent<T extends AbstractPlayerRank<?>> extends AbstractPlayerRankEvent<T>
{

    @NotNull ExpiryCause getExpiryCause();

    enum ExpiryCause {
        MANUAL,
        AUTOMATIC
    }
}
