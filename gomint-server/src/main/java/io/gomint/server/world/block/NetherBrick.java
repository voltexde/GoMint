package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class NetherBrick extends Block {

    @Override
    public int getBlockId() {
        return 112;
    }

    @Override
    public long getBreakTime() {
        return 3000;
    }

}
