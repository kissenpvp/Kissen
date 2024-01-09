package net.kissenpvp.core.api.user.rank.event;

import net.kissenpvp.core.api.user.rank.Rank;
import net.kyori.adventure.text.Component;

public interface RankSetSuffixEvent<T extends Rank> extends RankSetEvent<T, Component> {}
