package io.gomint.server.inventory.item;

import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
public abstract class ItemArmor extends ItemStack {

    // CHECKSTYLE:OFF
    public ItemArmor( int material, short data, int amount ) {
        super( material, data, amount );
    }

    public ItemArmor( int material, short data, int amount, NBTTagCompound nbt ) {
        super( material, data, amount, nbt );
    }
    // CHECKSTYLE:ON

    public abstract float getReductionValue();

}
