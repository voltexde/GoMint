package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Cobblestone extends Block {

    @Override
    public int getBlockId() {
        return 4;
    }

    @Override
    public long getBreakTime() {
        return 3000;
    }

}
