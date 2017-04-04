package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Cocoa extends Block {

    @Override
    public int getBlockId() {
        return 127;
    }

    @Override
    public long getBreakTime() {
        return 300;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

}
