package io.gomint.server.inventory.item.generator;

import io.gomint.server.inventory.item.ItemScute;
import io.gomint.taglib.NBTTagCompound;

public class ItemScuteGenerator implements ItemGenerator {

    @Override
    public ItemScute generate( short data, byte amount, NBTTagCompound nbt ) {
        return new ItemScute( data, amount, nbt );
    }

    @Override
    public ItemScute generate( short data, byte amount ) {
        return new ItemScute( data, amount );
    }
}
