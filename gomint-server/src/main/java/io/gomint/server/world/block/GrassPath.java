package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class GrassPath extends Block {

    @Override
    public int getBlockId() {
        return 198;
    }

    @Override
    public long getBreakTime() {
        return 900;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

}
