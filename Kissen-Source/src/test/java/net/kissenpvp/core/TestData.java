package net.kissenpvp.core;

import org.jetbrains.annotations.NotNull;

public record TestData<K, V>(K request, @NotNull V expected, boolean equalsResult)
{
    public TestData(K entityID, @NotNull V expected)
    {
        this(entityID, expected, true);
    }
}
