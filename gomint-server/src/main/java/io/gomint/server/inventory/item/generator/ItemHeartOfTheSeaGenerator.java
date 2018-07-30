package io.gomint.server.inventory.item.generator;

import io.gomint.server.inventory.item.ItemHeartOfTheSea;
import io.gomint.taglib.NBTTagCompound;

public class ItemHeartOfTheSeaGenerator implements ItemGenerator {

    @Override
    public ItemHeartOfTheSea generate( short data, byte amount, NBTTagCompound nbt ) {
        return new ItemHeartOfTheSea( data, amount, nbt );
    }

    @Override
    public ItemHeartOfTheSea generate( short data, byte amount ) {
        return new ItemHeartOfTheSea( data, amount );
    }
}
