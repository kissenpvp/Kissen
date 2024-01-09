package net.kissenpvp.core.api.user.rank.event;

import net.kissenpvp.core.api.user.rank.Rank;
import net.kyori.adventure.text.format.TextColor;

public interface RankSetChatColorEvent<T extends Rank> extends RankSetEvent<T, TextColor> {}
