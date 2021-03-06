package io.gomint.server.inventory.item;
import io.gomint.inventory.item.ItemType;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 18 )
 public class ItemLeaves extends ItemStack implements io.gomint.inventory.item.ItemLeaves {



    @Override
    public String getBlockId() {
        return "minecraft:leaves";
    }

    @Override
    public ItemType getType() {
        return ItemType.LEAVES;
    }

}
