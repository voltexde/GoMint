package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Flower extends Block {

    @Override
    public int getBlockId() {
        return 38;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

}
