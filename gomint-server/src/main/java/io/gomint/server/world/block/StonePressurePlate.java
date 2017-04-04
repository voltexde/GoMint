package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class StonePressurePlate extends Block {

    @Override
    public int getBlockId() {
        return 70;
    }

    @Override
    public long getBreakTime() {
        return 750;
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
