package io.gomint.server.world.block;

import io.gomint.server.registry.RegisterInfo;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 219 )
public class PurpleGlazedTerracotta extends Block {

    @Override
    public int getBlockId() {
        return 219;
    }

    @Override
    public long getBreakTime() {
        return 2100;
    }

}
