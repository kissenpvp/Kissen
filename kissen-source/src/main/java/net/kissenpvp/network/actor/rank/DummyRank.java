package net.kissenpvp.network.actor.rank;

import org.jetbrains.annotations.NotNull;

public class DummyRank extends InternalRank
{
    public DummyRank()
    {
        super("player", Integer.MAX_VALUE / 2); // TODO: might revisit the priority of this rank.
    }

    @Override public void priority(int priority)
    {
        throw new UnsupportedOperationException("Cannot change priority of dummy rank.");
    }
}
