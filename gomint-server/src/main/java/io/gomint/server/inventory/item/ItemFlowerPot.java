package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 140 )
 public class ItemFlowerPot extends ItemStack implements io.gomint.inventory.item.ItemFlowerPot {

    // CHECKSTYLE:OFF
    public ItemFlowerPot( short data, int amount ) {
        super( 140, data, amount );
    }

    public ItemFlowerPot( short data, int amount, NBTTagCompound nbt ) {
        super( 140, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
