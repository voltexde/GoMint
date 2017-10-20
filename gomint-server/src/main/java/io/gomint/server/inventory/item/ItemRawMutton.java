package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 423 )
public class ItemRawMutton extends ItemFood implements io.gomint.inventory.item.ItemRawMutton {

    // CHECKSTYLE:OFF
    public ItemRawMutton( short data, int amount ) {
        super( 423, data, amount );
    }

    public ItemRawMutton( short data, int amount, NBTTagCompound nbt ) {
        super( 423, data, amount, nbt );
    }
    // CHECKSTYLE:ON

    @Override
    public float getSaturation() {
        return 0.3f;
    }

    @Override
    public float getHunger() {
        return 2;
    }

}
