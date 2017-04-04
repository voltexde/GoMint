package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class StoneSlab extends Block {

    @Override
    public int getBlockId() {
        return 44;
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
