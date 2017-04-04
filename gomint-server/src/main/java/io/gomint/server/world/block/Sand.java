package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Sand extends Block {

    @Override
    public int getBlockId() {
        return 12;
    }

    @Override
    public long getBreakTime() {
        return 750;
    }

}
