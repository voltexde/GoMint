package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 197 )
 public class ItemDarkOakDoor extends ItemStack implements io.gomint.inventory.item.ItemDarkOakDoor {

    // CHECKSTYLE:OFF
    public ItemDarkOakDoor( short data, int amount ) {
        super( 197, data, amount );
    }

    public ItemDarkOakDoor( short data, int amount, NBTTagCompound nbt ) {
        super( 197, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
