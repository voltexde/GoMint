package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Sponge extends Block {

    @Override
    public int getBlockId() {
        return 19;
    }

    @Override
    public long getBreakTime() {
        return 900;
    }

}
