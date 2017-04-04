package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class DoubleRedSandstoneSlab extends Block {

    @Override
    public int getBlockId() {
        return 181;
    }

    @Override
    public long getBreakTime() {
        return 3000;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

}
