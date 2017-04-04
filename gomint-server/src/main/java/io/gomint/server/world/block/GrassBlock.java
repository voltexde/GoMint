package io.gomint.server.world.block;

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
    public long update( long currentTimeMS, float dT ) {
        Block block = world.getBlockAt( location.add( 0, 1, 0 ) );
        byte lightLevel = block.getSkyLightLevel();

        if ( lightLevel >= 9 ) {

        }

        return currentTimeMS + 50;
    }

    @Override
    public long getBreakTime() {
        return 900;
    }

}
