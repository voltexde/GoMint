package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 64 )
 public class ItemWoodenDoor extends ItemStack implements io.gomint.inventory.item.ItemWoodenDoor {

    // CHECKSTYLE:OFF
    public ItemWoodenDoor( short data, int amount ) {
        super( 64, data, amount );
    }

    public ItemWoodenDoor( short data, int amount, NBTTagCompound nbt ) {
        super( 64, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
