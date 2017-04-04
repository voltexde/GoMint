package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Air extends Block implements io.gomint.world.block.Air {

    @Override
    public int getBlockId() {
        return 0;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

}
