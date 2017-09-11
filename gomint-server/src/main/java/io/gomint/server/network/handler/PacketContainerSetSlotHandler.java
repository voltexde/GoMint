package io.gomint.server.network.handler;

import io.gomint.server.inventory.item.ItemStack;
import io.gomint.server.inventory.transaction.DropItemTransaction;
import io.gomint.server.inventory.transaction.InventoryTransaction;
import io.gomint.server.inventory.transaction.Transaction;
import io.gomint.server.inventory.transaction.TransactionGroup;
import io.gomint.server.network.PlayerConnection;
import io.gomint.server.network.packet.PacketInventorySetSlot;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PacketContainerSetSlotHandler implements PacketHandler<PacketInventorySetSlot> {

    @Override
    public void handle( PacketInventorySetSlot packet, long currentTimeMillis, PlayerConnection connection ) {
        // Exception safety
        if ( packet.getSlot() < 0 ) {
            return;
        }

        // Begin transaction
        Transaction transaction;
        if ( packet.getWindowId() == 0 ) { // Player inventory
            // The client wanted to set a slot which is outside of the inventory size
            if ( packet.getSlot() >= connection.getEntity().getInventory().getSize() ) {
                return;
            }

            transaction = new InventoryTransaction(
                    connection.getEntity().getInventory(),
                    packet.getSlot(),
                    ( (ItemStack) connection.getEntity().getInventory().getItem( packet.getSlot() ) ).clone(),
                    ( (ItemStack) packet.getItemStack() ).clone(),
                    currentTimeMillis );
        } else if ( packet.getWindowId() == 0x78 ) { // Armor inventory
            // The client wanted to set a slot which is outside of the inventory size
            if ( packet.getSlot() >= 4 ) {
                return;
            }

            transaction = new InventoryTransaction( connection.getEntity().getInventory(),
                    packet.getSlot() + connection.getEntity().getInventory().getSize(),
                    connection.getEntity().getInventory().getItem( packet.getSlot() + connection.getEntity().getInventory().getSize() ),
                    ( (ItemStack) packet.getItemStack() ).clone(),
                    currentTimeMillis );
        } else {
            return;
        }

        // Check if the client changed something
        if ( transaction.getSourceItem().equals( transaction.getTargetItem() ) &&
                transaction.getTargetItem().getAmount() == transaction.getSourceItem().getAmount() ) {
            return;
        }

        // Check if we need a new transaction and auto cancel transactions which are open for at least 8 seconds
        if ( connection.getEntity().getTransactions() == null ) {
            connection.getEntity().setTransactions( new TransactionGroup( connection.getEntity() ) );
        }

        // Hacky way of item drops, TODO: remove this after mojang did inventory stuff properly
        if ( connection.getEntity().getQueuedItemDrop() != null ) {
            connection.getEntity().getTransactions().addTransaction( new DropItemTransaction( connection.getEntity().getQueuedItemDrop(), connection.getEntity().getQueuedItemDrop().getItemStack(), currentTimeMillis ) );
            connection.getEntity().setQueuedItemDrop( null );
        }

        // Add transaction for inventory slot change
        connection.getEntity().getTransactions().addTransaction( transaction );

        // Can we execute now?
        connection.getEntity().getTransactions().tryExecute( currentTimeMillis );

    }

}
