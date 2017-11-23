package io.gomint.server.inventory.item;

import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
public abstract class ItemBanner extends ItemStack {

    // CHECKSTYLE:OFF
    public ItemBanner( int material, short data, int amount ) {
        super( material, data, amount );
    }

    public ItemBanner( int material, short data, int amount, NBTTagCompound nbt ) {
        super( material, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
