package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Snow extends Block {

    @Override
    public int getBlockId() {
        return 80;
    }

    @Override
    public long getBreakTime() {
        return 300;
    }

}
