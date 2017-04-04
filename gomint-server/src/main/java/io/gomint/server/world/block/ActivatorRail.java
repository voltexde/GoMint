package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class ActivatorRail extends Block {

    @Override
    public int getBlockId() {
        return 126;
    }

    @Override
    public long getBreakTime() {
        return 1050;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

}
