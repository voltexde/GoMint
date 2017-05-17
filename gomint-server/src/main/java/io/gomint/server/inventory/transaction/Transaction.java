package io.gomint.server.inventory.transaction;

import io.gomint.inventory.ItemStack;
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
public class Transaction {

    private final Inventory inventory;
    private final int slot;
    private final ItemStack sourceItem;
    private final ItemStack targetItem;
    private final long creationTime;

}
