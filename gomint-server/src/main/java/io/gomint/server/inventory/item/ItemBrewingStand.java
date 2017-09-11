package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 117 )
 public class ItemBrewingStand extends ItemStack implements io.gomint.inventory.item.ItemBrewingStand {

    // CHECKSTYLE:OFF
    public ItemBrewingStand( short data, int amount ) {
        super( 117, data, amount );
    }

    public ItemBrewingStand( short data, int amount, NBTTagCompound nbt ) {
        super( 117, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
