package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class WoodenDoor extends Block {

    @Override
    public int getBlockId() {
        return 64;
    }

    @Override
    public long getBreakTime() {
        return 4500;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

}
