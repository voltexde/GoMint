package io.gomint.server.world.block;

import io.gomint.world.block.BlockLiquid;

/**
 * @author geNAZt
 */
public abstract class Liquid extends Block implements BlockLiquid {

    public float getFillHeight() {
        short data = this.getBlockData();
        if ( data > 8 ) {
            data = 8;
        }

        return ( ( data + 1 ) / 9f );
    }

}
