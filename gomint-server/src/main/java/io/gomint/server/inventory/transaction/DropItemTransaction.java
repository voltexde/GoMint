package io.gomint.server.inventory.transaction;

import io.gomint.inventory.item.ItemStack;
import io.gomint.server.entity.passive.EntityItem;
import io.gomint.server.inventory.Inventory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author geNAZt
 * @version 1.0
 */
@AllArgsConstructor
@Getter
@ToString
public class DropItemTransaction implements Transaction {

    private final EntityItem itemDrop;
    private final ItemStack targetItem;
    private final long creationTime;

    @Override
    public boolean hasInventory() {
        return false;
    }

    @Override
    public ItemStack getSourceItem() {
        return null;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    @Override
    public int getSlot() {
        return -1;
    }

}
