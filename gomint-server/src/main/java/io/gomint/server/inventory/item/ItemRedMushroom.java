package io.gomint.server.inventory.item;
import io.gomint.inventory.item.ItemType;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 40 )
 public class ItemRedMushroom extends ItemStack implements io.gomint.inventory.item.ItemRedMushroom {



    @Override
    public String getBlockId() {
        return "minecraft:red_mushroom";
    }

    @Override
    public ItemType getType() {
        return ItemType.RED_MUSHROOM;
    }

}
