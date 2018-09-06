package io.gomint.server.entity;

import io.gomint.server.registry.SkipRegister;
import lombok.Getter;

/**
 * @author geNAZt
 * @version 1.0
 */
@SkipRegister
public enum EntityEvent {

    HURT( 2 ),
    DEATH( 3 ),

    RESPAWN( 18 );

    @Getter
    private final byte id;

    EntityEvent( int id ) {
        this.id = (byte) id;
    }

}
