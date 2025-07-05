package net.kissenpvp.api.database;

public interface SubscriptionRepository<P, X, T extends PersistableEntity<X>, Y extends SubscriptionEntity<P, X, T>> extends Repository<P, Y>
{

}
