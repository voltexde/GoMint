package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Cauldron extends Block {

    @Override
    public int getBlockId() {
        return 118;
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
