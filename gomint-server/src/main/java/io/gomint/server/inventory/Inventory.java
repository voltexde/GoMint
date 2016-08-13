package io.gomint.server.inventory;

import io.gomint.inventory.ItemStack;

/**
 * @author geNAZt
 * @version 1.0
 */
public abstract class Inventory {

    protected int size;
    protected ItemStack[] contents;

    public Inventory( int size ) {
        this.size = size;
        this.contents = new ItemStack[size];
    }

    public void setItem( int index, ItemStack item ) {
        this.contents[index] = item;
        this.sendContents( index );
    }

    protected abstract void sendContents( int index );

}
