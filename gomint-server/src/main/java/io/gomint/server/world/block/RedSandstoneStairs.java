package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class RedSandstoneStairs extends Block {

    @Override
    public int getBlockId() {
        return 180;
    }

    @Override
    public long getBreakTime() {
        return 1200;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

}
