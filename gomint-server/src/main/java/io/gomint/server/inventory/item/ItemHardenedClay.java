package io.gomint.server.inventory.item;
import io.gomint.inventory.item.ItemType;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 172 )
 public class ItemHardenedClay extends ItemStack implements io.gomint.inventory.item.ItemHardenedClay {



    @Override
    public String getBlockId() {
        return "minecraft:hardened_clay";
    }

    @Override
    public ItemType getType() {
        return ItemType.HARDENED_CLAY;
    }

}
