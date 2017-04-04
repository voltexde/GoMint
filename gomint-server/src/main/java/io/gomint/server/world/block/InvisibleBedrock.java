package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class InvisibleBedrock extends Block {

    @Override
    public int getBlockId() {
        return 95;
    }

    @Override
    public long getBreakTime() {
        return -1;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

}
