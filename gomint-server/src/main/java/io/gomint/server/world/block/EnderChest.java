package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class EnderChest extends Block {

    @Override
    public int getBlockId() {
        return 130;
    }

    @Override
    public long getBreakTime() {
        return 33750;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

}
