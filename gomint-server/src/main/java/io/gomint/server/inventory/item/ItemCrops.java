package io.gomint.server.inventory.item;

import io.gomint.inventory.item.ItemType;
import io.gomint.server.registry.RegisterInfo;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 295 )
public class ItemCrops extends ItemStack implements io.gomint.inventory.item.ItemCrops {

    @Override
    public String getBlockId() {
        return "minecraft:wheat";
    }

    @Override
    public ItemType getType() {
        return ItemType.CROPS;
    }

}
