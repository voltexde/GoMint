package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 193 )
 public class ItemSpruceDoor extends ItemStack implements io.gomint.inventory.item.ItemSpruceDoor {

    // CHECKSTYLE:OFF
    public ItemSpruceDoor( short data, int amount ) {
        super( 193, data, amount );
    }

    public ItemSpruceDoor( short data, int amount, NBTTagCompound nbt ) {
        super( 193, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
