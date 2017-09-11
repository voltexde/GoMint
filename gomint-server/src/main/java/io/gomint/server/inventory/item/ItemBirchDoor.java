package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 194 )
 public class ItemBirchDoor extends ItemStack implements io.gomint.inventory.item.ItemBirchDoor {

    // CHECKSTYLE:OFF
    public ItemBirchDoor( short data, int amount ) {
        super( 194, data, amount );
    }

    public ItemBirchDoor( short data, int amount, NBTTagCompound nbt ) {
        super( 194, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
