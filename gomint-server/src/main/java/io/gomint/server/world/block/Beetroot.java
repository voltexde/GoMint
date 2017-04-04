package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Beetroot extends Block {

    @Override
    public int getBlockId() {
        return 244;
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
