package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Tripwire extends Block {

    @Override
    public int getBlockId() {
        return 132;
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
