package io.gomint.server.world.block;

import io.gomint.server.registry.RegisterInfo;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 172 )
public class HardenedClay extends Block {

    @Override
    public int getBlockId() {
        return 172;
    }

    @Override
    public long getBreakTime() {
        return 1875;
    }

    @Override
    public float getBlastResistance() {
        return 7.0f;
    }

}