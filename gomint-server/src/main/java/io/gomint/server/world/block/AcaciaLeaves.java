package io.gomint.server.world.block;

import io.gomint.inventory.item.ItemShears;
import io.gomint.inventory.item.ItemStack;
import io.gomint.inventory.item.ItemSword;
import io.gomint.server.registry.RegisterInfo;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 161 )
public class AcaciaLeaves extends Block {

    @Override
    public int getBlockId() {
        return 161;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public long getFinalBreakTime( ItemStack item ) {
        // Leaves are a bit of a special case here
        if ( item instanceof ItemShears ) {
            return 50;
        } else if ( item instanceof ItemSword ) {
            return 200;
        } else {
            return 350;
        }
    }

    @Override
    public float getBlastResistance() {
        return 1.0f;
    }

}