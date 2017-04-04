package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class BlockOfGold extends Block {

    @Override
    public int getBlockId() {
        return 41;
    }

    @Override
    public long getBreakTime() {
        return 4500;
    }

}
