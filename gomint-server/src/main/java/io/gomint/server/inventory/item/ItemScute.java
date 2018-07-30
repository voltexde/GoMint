package io.gomint.server.inventory.item;

import io.gomint.inventory.item.ItemType;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

@RegisterInfo( id = 468 )
public class ItemScute extends ItemStack implements io.gomint.inventory.item.ItemScute {

    // CHECKSTYLE:OFF
    public ItemScute( short data, int amount ) {
        super( 468, data, amount );
    }

    public ItemScute( short data, int amount, NBTTagCompound nbt ) {
        super( 468, data, amount, nbt );
    }
    // CHECKSTYLE:ON

    @Override
    public ItemType getType() {
        return ItemType.SCUTE;
    }
}
