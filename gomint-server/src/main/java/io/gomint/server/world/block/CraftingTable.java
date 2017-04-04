package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class CraftingTable extends Block {

    @Override
    public int getBlockId() {
        return 58;
    }

    @Override
    public long getBreakTime() {
        return 3750;
    }

}
