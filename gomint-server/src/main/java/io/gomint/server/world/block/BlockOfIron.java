package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class BlockOfIron extends Block {

    @Override
    public int getBlockId() {
        return 42;
    }

    @Override
    public long getBreakTime() {
        return 7500;
    }

}
