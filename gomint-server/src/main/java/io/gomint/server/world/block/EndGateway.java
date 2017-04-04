package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class EndGateway extends Block {

    @Override
    public int getBlockId() {
        return 209;
    }

    @Override
    public long getBreakTime() {
        return -1;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

}
