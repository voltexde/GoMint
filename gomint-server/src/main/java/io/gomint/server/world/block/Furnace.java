package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Furnace extends Block {

    @Override
    public int getBlockId() {
        return 61;
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
