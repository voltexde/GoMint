package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Vines extends Block {

    @Override
    public int getBlockId() {
        return 106;
    }

    @Override
    public long getBreakTime() {
        return 300;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

}
