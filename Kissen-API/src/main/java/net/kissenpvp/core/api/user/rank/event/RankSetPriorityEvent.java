package net.kissenpvp.core.api.user.rank.event;

import net.kissenpvp.core.api.user.rank.AbstractRank;

public interface RankSetPriorityEvent<T extends AbstractRank> extends RankSetEvent<T, Integer> {}
