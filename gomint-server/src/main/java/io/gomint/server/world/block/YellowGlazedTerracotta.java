package io.gomint.server.world.block;

import io.gomint.server.registry.RegisterInfo;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 224 )
public class YellowGlazedTerracotta extends Block {

    @Override
    public int getBlockId() {
        return 224;
    }

    @Override
    public long getBreakTime() {
        return 2100;
    }

}
