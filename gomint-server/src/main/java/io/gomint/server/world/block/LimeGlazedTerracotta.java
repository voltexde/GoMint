package io.gomint.server.world.block;

import io.gomint.server.registry.RegisterInfo;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 225 )
public class LimeGlazedTerracotta extends Block {

    @Override
    public int getBlockId() {
        return 225;
    }

    @Override
    public long getBreakTime() {
        return 2100;
    }

}
