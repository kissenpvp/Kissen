package net.kissenpvp.network.actor.rank;

import net.kissenpvp.api.network.actor.rank.DefaultRank;

public class DummyRank extends InternalRank implements DefaultRank
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
