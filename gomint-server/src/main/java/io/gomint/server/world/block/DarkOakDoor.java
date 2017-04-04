package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class DarkOakDoor extends Block {

    @Override
    public int getBlockId() {
        return 197;
    }

    @Override
    public long getBreakTime() {
        return 4500;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

}
