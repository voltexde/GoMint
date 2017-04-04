package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Obsidian extends Block {

    @Override
    public int getBlockId() {
        return 49;
    }

    @Override
    public long getBreakTime() {
        return 75000;
    }

}
