package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 26 )
 public class ItemBed extends ItemStack implements io.gomint.inventory.item.ItemBed {

    // CHECKSTYLE:OFF
    public ItemBed( short data, int amount ) {
        super( 26, data, amount );
    }

    public ItemBed( short data, int amount, NBTTagCompound nbt ) {
        super( 26, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
