package net.kissenpvp.core.api.user.rank.event;

import net.kissenpvp.core.api.user.rank.AbstractRank;
import net.kyori.adventure.text.Component;

public interface RankSetPrefixEvent<T extends AbstractRank> extends RankSetEvent<T, Component> {}
