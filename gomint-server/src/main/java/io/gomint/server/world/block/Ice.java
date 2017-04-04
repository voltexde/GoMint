package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Ice extends Block {

    @Override
    public int getBlockId() {
        return 79;
    }

    @Override
    public long getBreakTime() {
        return 750;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

}
