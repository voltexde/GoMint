package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class BlockOfRedstone extends Block {

    @Override
    public int getBlockId() {
        return 152;
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
