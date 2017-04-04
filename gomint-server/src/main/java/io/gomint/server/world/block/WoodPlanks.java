package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class WoodPlanks extends Block {

    @Override
    public int getBlockId() {
        return 5;
    }

    @Override
    public long getBreakTime() {
        return 3000;
    }

}
