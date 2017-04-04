package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Observer extends Block {

    @Override
    public int getBlockId() {
        return 251;
    }

    @Override
    public long getBreakTime() {
        return 5250;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

}
