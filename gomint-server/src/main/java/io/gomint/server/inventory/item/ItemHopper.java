package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 154 )
 public class ItemHopper extends ItemStack implements io.gomint.inventory.item.ItemHopper {

    // CHECKSTYLE:OFF
    public ItemHopper( short data, int amount ) {
        super( 154, data, amount );
    }

    public ItemHopper( short data, int amount, NBTTagCompound nbt ) {
        super( 154, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
