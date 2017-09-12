package io.gomint.server.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author geNAZt
 * @version 1.0
 */
@AllArgsConstructor
@Getter
public enum WindowMagicNumbers {

    PLAYER( 0 ),
    OFFHAND( 119 ),
    ARMOR( 120 ),
    CREATIVE( 121 ),
    CURSOR( 124 );

    private final int id;
}
