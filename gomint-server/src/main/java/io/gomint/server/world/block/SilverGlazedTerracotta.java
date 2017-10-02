package io.gomint.server.world.block;

import io.gomint.server.registry.RegisterInfo;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 228 )
public class SilverGlazedTerracotta extends Block {

    @Override
    public int getBlockId() {
        return 228;
    }

    @Override
    public long getBreakTime() {
        return 2100;
    }

}
