package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class MobHead extends Block {

    @Override
    public int getBlockId() {
        return 144;
    }

    @Override
    public long getBreakTime() {
        return 1500;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

}
