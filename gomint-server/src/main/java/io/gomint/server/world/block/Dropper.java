package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Dropper extends Block {

    @Override
    public int getBlockId() {
        return 125;
    }

    @Override
    public long getBreakTime() {
        return 5250;
    }

}
