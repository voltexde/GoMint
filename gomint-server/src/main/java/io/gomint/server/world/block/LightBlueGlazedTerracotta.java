package io.gomint.server.world.block;

import io.gomint.server.registry.RegisterInfo;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 223 )
public class LightBlueGlazedTerracotta extends Block {

    @Override
    public int getBlockId() {
        return 223;
    }

    @Override
    public long getBreakTime() {
        return 2100;
    }

}
