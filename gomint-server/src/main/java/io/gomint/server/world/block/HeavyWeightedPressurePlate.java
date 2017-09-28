package io.gomint.server.world.block;

import io.gomint.server.inventory.item.ItemHeavyWeightedPressurePlate;
import io.gomint.server.registry.RegisterInfo;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 148 )
public class HeavyWeightedPressurePlate extends Block {

    @Override
    public int getBlockId() {
        return 148;
    }

    @Override
    public long getBreakTime() {
        return 750;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

}
