package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Cobweb extends Block {

    @Override
    public int getBlockId() {
        return 30;
    }

    @Override
    public long getBreakTime() {
        return 6000;
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
