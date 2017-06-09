package io.gomint.server.world.block;

import io.gomint.server.world.UpdateReason;

/**
 * @author geNAZt
 * @version 1.0
 */
public class GrassBlock extends Block {

    @Override
    public int getBlockId() {
        return 2;
    }

    @Override
    public long update( UpdateReason updateReason, long currentTimeMS, float dT ) {
        Block block = world.getBlockAt( location.add( 0, 1, 0 ) );
        byte lightLevel = block.getSkyLightLevel();

        if ( lightLevel >= 9 ) {

        }

        return -1;
    }

    @Override
    public long getBreakTime() {
        return 900;
    }

}
