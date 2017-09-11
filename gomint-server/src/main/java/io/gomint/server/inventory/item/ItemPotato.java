package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 142 )
 public class ItemPotato extends ItemStack implements io.gomint.inventory.item.ItemPotato {

    // CHECKSTYLE:OFF
    public ItemPotato( short data, int amount ) {
        super( 142, data, amount );
    }

    public ItemPotato( short data, int amount, NBTTagCompound nbt ) {
        super( 142, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
