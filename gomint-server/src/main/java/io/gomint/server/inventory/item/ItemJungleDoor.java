package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 195 )
 public class ItemJungleDoor extends ItemStack implements io.gomint.inventory.item.ItemJungleDoor {

    // CHECKSTYLE:OFF
    public ItemJungleDoor( short data, int amount ) {
        super( 195, data, amount );
    }

    public ItemJungleDoor( short data, int amount, NBTTagCompound nbt ) {
        super( 195, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
