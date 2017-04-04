package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class IronOre extends Block {

    @Override
    public int getBlockId() {
        return 15;
    }

    @Override
    public long getBreakTime() {
        return 4500;
    }

}
