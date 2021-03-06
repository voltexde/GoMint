package io.gomint.server.inventory.item;
import io.gomint.inventory.item.ItemType;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 171 )
 public class ItemCarpet extends ItemStack implements io.gomint.inventory.item.ItemCarpet {



    @Override
    public String getBlockId() {
        return "minecraft:carpet";
    }

    @Override
    public ItemType getType() {
        return ItemType.CARPET;
    }

}
