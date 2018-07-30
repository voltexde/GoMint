package io.gomint.server.inventory.item;

import io.gomint.inventory.item.ItemType;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

@RegisterInfo( id = 467 )
public class ItemHeartOfTheSea extends ItemStack implements io.gomint.inventory.item.ItemHeartOfTheSea {

    // CHECKSTYLE:OFF
    public ItemHeartOfTheSea( short data, int amount ) {
        super( 467, data, amount );
    }

    public ItemHeartOfTheSea( short data, int amount, NBTTagCompound nbt ) {
        super( 467, data, amount, nbt );
    }
    // CHECKSTYLE:ON

    @Override
    public ItemType getType() {
        return ItemType.HEART_OF_THE_SEA;
    }
}
