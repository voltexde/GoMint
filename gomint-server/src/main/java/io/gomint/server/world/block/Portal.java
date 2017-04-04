package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Portal extends Block {

    @Override
    public int getBlockId() {
        return 90;
    }

    @Override
    public long getBreakTime() {
        return -1;
    }

}
