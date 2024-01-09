package net.kissenpvp.core.api.user.rank.event;

import net.kissenpvp.core.api.user.rank.Rank;

public interface RankSetPriorityEvent<T extends Rank> extends RankSetEvent<T, Integer> {}
