package net.kissenpvp.database;

import net.kissenpvp.api.database.PersistableEntity;


public abstract class InternalPersistableEntity<P> implements PersistableEntity<P>
{
    private int storedSignature;

    public int storedSignature()
    {
        return storedSignature;
    }

    public void overrideSignature()
    {
        storedSignature = signature();
    }

    @Override public boolean unsavedChanges()
    {
        return storedSignature != signature();
    }
}
