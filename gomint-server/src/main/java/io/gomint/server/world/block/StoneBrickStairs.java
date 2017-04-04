package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class StoneBrickStairs extends Block {

    @Override
    public int getBlockId() {
        return 109;
    }

    @Override
    public long getBreakTime() {
        return 2250;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

}
