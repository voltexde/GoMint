package io.gomint.server.world.block;

import io.gomint.server.registry.RegisterInfo;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 227 )
public class GrayGlazedTerracotta extends Block {

    @Override
    public int getBlockId() {
        return 227;
    }

    @Override
    public long getBreakTime() {
        return 2100;
    }

}
