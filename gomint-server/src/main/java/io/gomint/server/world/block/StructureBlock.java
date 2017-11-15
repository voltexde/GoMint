package io.gomint.server.world.block;

import io.gomint.server.registry.RegisterInfo;

/**
 * @author geNAZt
 */
@RegisterInfo( id = 252 )
public class StructureBlock extends Block {

    @Override
    public int getBlockId() {
        return 252;
    }

    @Override
    public float getBlastResistance() {
        return 1.8E7f;
    }

}
