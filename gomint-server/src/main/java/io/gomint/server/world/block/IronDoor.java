package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class IronDoor extends Door {

    @Override
    public int getBlockId() {
        return 71;
    }

    @Override
    public long getBreakTime() {
        return 7500;
    }

}
