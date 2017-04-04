package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class StainedGlass extends Block {

    @Override
    public int getBlockId() {
        return 241;
    }

    @Override
    public long getBreakTime() {
        return 450;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

}
