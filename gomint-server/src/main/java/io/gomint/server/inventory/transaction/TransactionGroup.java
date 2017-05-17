package io.gomint.server.inventory.transaction;

import io.gomint.inventory.ItemStack;
import io.gomint.inventory.Material;
import io.gomint.server.inventory.Inventory;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author geNAZt
 * @version 1.0
 */
public class TransactionGroup {

    @Getter
    private final long creationTime;

    @Getter
    private boolean hasExecuted = false;
    @Getter
    private final Set<Inventory> inventories = new HashSet<>();
    private final Set<Transaction> transactions = new HashSet<>();

    // Need / have for this transactions
    @Getter
    private List<ItemStack> haveItems = new ArrayList<>();
    private List<ItemStack> needItems = new ArrayList<>();

    // Matched
    private boolean matchItems;

    /**
     * Generate a new transaction group
     */
    public TransactionGroup() {
        this.creationTime = System.currentTimeMillis();
    }

    /**
     * Add a new transaction to this group
     *
     * @param transaction The transaction which should be added
     */
    public void addTransaction( Transaction transaction ) {
        // Check if not already added
        if ( this.transactions.contains( transaction ) ) {
            return;
        }

        // Check if we have a older transaction which this should replace
        for ( Transaction tx : new HashSet<>( this.transactions ) ) {
            if ( tx.getInventory().equals( transaction.getInventory() ) && tx.getSlot() == transaction.getSlot() ) {
                if ( transaction.getCreationTime() >= tx.getCreationTime() ) {
                    this.transactions.remove( tx );
                } else {
                    return;
                }
            }
        }

        // Add this transaction and also the inventory
        this.transactions.add( transaction );
        this.inventories.add( transaction.getInventory() );
    }

    private void calcMatchItems() {
        // Clear both sides for a fresh compare
        this.haveItems.clear();
        this.needItems.clear();

        // Check all transactions for needed and having items
        for ( Transaction ts : this.transactions ) {
            if ( ts.getTargetItem().getMaterial() != Material.AIR ) {
                this.needItems.add( ts.getTargetItem().clone() );
            }

            ItemStack checkSourceItem = ts.getInventory().getItem( ts.getSlot() );
            ItemStack sourceItem = ts.getSourceItem().clone();

            // Check if source inventory changed during transaction
            if ( !checkSourceItem.deepEquals( sourceItem ) || sourceItem.getAmount() != checkSourceItem.getAmount() ) {
                this.matchItems = false;
                return;
            }

            if ( sourceItem.getMaterial() != Material.AIR ) {
                this.haveItems.add( sourceItem );
            }
        }

        // Now check if we have items left which are needed
        for ( ItemStack needItem : new ArrayList<>( this.needItems ) ) {
            for ( ItemStack haveItem : new ArrayList<>( this.haveItems ) ) {
                if ( needItem.deepEquals( haveItem ) ) {
                    int amount = Math.min( haveItem.getAmount(), needItem.getAmount() );
                    needItem.setAmount( needItem.getAmount() - amount );
                    haveItem.setAmount( haveItem.getAmount() - amount );

                    if ( haveItem.getAmount() == 0 ) {
                        this.haveItems.remove( haveItem );
                    }

                    if ( needItem.getAmount() == 0 ) {
                        this.needItems.remove( needItem );
                        break;
                    }
                }
            }
        }

        this.matchItems = true;
    }

    /**
     * Check if transaction is complete and can be executed
     *
     * @return true if the transaction is complete and can be executed
     */
    public boolean canExecute() {
        this.calcMatchItems();
        return this.matchItems && this.haveItems.isEmpty() && this.needItems.isEmpty() && !this.transactions.isEmpty();
    }

    /**
     * Try to execute the transaction without force
     *
     * @return true on success, false otherwise
     */
    public boolean execute() {
        return execute( false );
    }

    /**
     * Try to execute the transaction
     *
     * @param force Should force be applied?
     * @return true on success, false otherwise
     */
    public boolean execute( boolean force ) {
        if ( this.hasExecuted || ( !force && !this.canExecute() ) ) {
            return false;
        }

        // TODO: Add a inventory event here

        for ( Transaction transaction : this.transactions ) {
            transaction.getInventory().setItem( transaction.getSlot(), transaction.getTargetItem() );
        }

        this.hasExecuted = true;
        return true;
    }

    /**
     * Predict if this itemstack would consume the have item side of the transaction
     *
     * @param itemStack The itemstack which may consume the have side
     * @return true if it can consume, false if not
     */
    public boolean predictConsume( ItemStack itemStack ) {
        if ( this.haveItems.isEmpty() ) {
            this.calcMatchItems();
        }

        for ( ItemStack haveItem : this.haveItems ) {
            if ( haveItem.deepEquals( itemStack ) && haveItem.getAmount() >= itemStack.getAmount() ) {
                return true;
            }
        }

        return false;
    }

}
