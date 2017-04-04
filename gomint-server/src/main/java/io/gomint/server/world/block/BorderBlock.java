package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class BorderBlock extends Block {

    @Override
    public int getBlockId() {
        return 212;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

}
