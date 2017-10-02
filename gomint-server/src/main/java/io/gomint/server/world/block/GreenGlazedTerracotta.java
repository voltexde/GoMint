package io.gomint.server.world.block;

import io.gomint.server.registry.RegisterInfo;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 233 )
public class GreenGlazedTerracotta extends Block {

    @Override
    public int getBlockId() {
        return 233;
    }

    @Override
    public long getBreakTime() {
        return 2100;
    }

}
