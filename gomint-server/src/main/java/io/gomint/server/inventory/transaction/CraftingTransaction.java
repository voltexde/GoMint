package io.gomint.server.inventory.transaction;

import io.gomint.server.inventory.item.ItemStack;
import io.gomint.server.inventory.Inventory;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author geNAZt
 * @version 1.0
 */
@AllArgsConstructor
@Getter
public class CraftingTransaction implements Transaction {

    private final ItemStack sourceItem;
    private final ItemStack targetItem;
    private final long creationTime;

    @Override
    public boolean hasInventory() {
        return false;
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
