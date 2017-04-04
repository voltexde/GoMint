package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Wool extends Block {

    @Override
    public int getBlockId() {
        return 35;
    }

    @Override
    public long getBreakTime() {
        return 1200;
    }

}
