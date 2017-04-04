package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class IronTrapdoor extends Block {

    @Override
    public int getBlockId() {
        return 167;
    }

    @Override
    public long getBreakTime() {
        return 7500;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

}
