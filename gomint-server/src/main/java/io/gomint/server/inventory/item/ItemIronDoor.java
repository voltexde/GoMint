package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 71 )
 public class ItemIronDoor extends ItemStack implements io.gomint.inventory.item.ItemIronDoor {

    // CHECKSTYLE:OFF
    public ItemIronDoor( short data, int amount ) {
        super( 71, data, amount );
    }

    public ItemIronDoor( short data, int amount, NBTTagCompound nbt ) {
        super( 71, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
