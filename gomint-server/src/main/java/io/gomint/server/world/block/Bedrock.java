package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Bedrock extends Block {

    @Override
    public int getBlockId() {
        return 7;
    }

    @Override
    public long getBreakTime() {
        return -1;
    }

}
