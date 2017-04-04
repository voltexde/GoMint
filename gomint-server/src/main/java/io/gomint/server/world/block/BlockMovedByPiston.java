package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class BlockMovedByPiston extends Block {

    @Override
    public int getBlockId() {
        return 250;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

}
