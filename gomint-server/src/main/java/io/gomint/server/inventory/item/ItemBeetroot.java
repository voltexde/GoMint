package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 244 )
 public class ItemBeetroot extends ItemStack implements io.gomint.inventory.item.ItemBeetroot {

    // CHECKSTYLE:OFF
    public ItemBeetroot( short data, int amount ) {
        super( 244, data, amount );
    }

    public ItemBeetroot( short data, int amount, NBTTagCompound nbt ) {
        super( 244, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
