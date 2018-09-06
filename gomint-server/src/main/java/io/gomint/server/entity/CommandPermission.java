package io.gomint.server.entity;

import io.gomint.server.registry.SkipRegister;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author geNAZt
 * @version 1.0
 */
@SkipRegister
@Getter
@AllArgsConstructor
public enum CommandPermission {

    NORMAL( 0 ),
    OPERATOR( 1 ),
    HOST( 2 ),
    AUTOMATION( 3 ),
    ADMIN( 4 );

    private int id;

}
