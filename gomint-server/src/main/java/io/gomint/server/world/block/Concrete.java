package io.gomint.server.world.block;

import io.gomint.server.registry.RegisterInfo;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 236 )
public class Concrete extends Block {

    @Override
    public int getBlockId() {
        return 236;
    }

    @Override
    public long getBreakTime() {
        return 2700;
    }
}
