package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class CobblestoneWall extends Block {

    @Override
    public int getBlockId() {
        return 139;
    }

    @Override
    public long getBreakTime() {
        return 3000;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

}
