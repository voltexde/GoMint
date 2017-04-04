package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PumpkinStem extends Block {

    @Override
    public int getBlockId() {
        return 104;
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
