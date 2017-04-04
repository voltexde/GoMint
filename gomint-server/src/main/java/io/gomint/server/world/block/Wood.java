package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Wood extends Block {

    @Override
    public int getBlockId() {
        return 17;
    }

    @Override
    public long getBreakTime() {
        return 3000;
    }

}
