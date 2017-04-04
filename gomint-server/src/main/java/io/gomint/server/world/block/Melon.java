package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Melon extends Block {

    @Override
    public int getBlockId() {
        return 103;
    }

    @Override
    public long getBreakTime() {
        return 1500;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

}
