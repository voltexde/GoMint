package io.gomint.server.world.block;

import io.gomint.server.registry.RegisterInfo;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 231 )
public class BlueGlazedTerracotta extends Block {

    @Override
    public int getBlockId() {
        return 231;
    }

    @Override
    public long getBreakTime() {
        return 2100;
    }

    @Override
    public float getBlastResistance() {
        return 7.0f;
    }

}