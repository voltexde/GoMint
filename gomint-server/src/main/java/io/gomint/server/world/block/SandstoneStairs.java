package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class SandstoneStairs extends Stairs {

    @Override
    public int getBlockId() {
        return 128;
    }

    @Override
    public long getBreakTime() {
        return 1200;
    }

}
