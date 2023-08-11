package net.kissenpvp.core.networking.client.entity;

import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;

public interface KissenServerEntity extends ServerEntity {

    @Override
    default boolean isClient() {
        return true;
    }
}
