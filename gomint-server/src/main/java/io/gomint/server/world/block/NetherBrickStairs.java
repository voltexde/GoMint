package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class NetherBrickStairs extends Stairs {

    @Override
    public int getBlockId() {
        return 114;
    }

    @Override
    public long getBreakTime() {
        return 3000;
    }

}
