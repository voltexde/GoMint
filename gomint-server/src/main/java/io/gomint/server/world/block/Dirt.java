package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Dirt extends Block implements io.gomint.world.block.Dirt {

    @Override
    public int getBlockId() {
        return 3;
    }

    @Override
    public long getBreakTime() {
        return 750;
    }

}
