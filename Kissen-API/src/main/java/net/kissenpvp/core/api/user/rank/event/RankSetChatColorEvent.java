package net.kissenpvp.core.api.user.rank.event;

import net.kissenpvp.core.api.user.rank.AbstractRank;
import net.kyori.adventure.text.format.TextColor;

public interface RankSetChatColorEvent<T extends AbstractRank> extends RankSetEvent<T, TextColor> {}
