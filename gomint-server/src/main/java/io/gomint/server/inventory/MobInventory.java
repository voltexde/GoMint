package io.gomint.server.inventory;

import io.gomint.inventory.ItemStack;

/**
 * @author geNAZt
 * @version 1.0
 *          <p>
 *          Inventory for any type of mob. This holds additional Armor Contents
 */
public abstract class MobInventory extends Inventory {

    protected ItemStack[] armorContents;

    protected MobInventory( int size ) {
        super( size );
        this.armorContents = new ItemStack[4];
    }

    public void setHelmet( ItemStack itemStack ) {
        this.armorContents[0] = itemStack;
        this.sendArmor( 0 );
    }

    public void setChestplate( ItemStack itemStack ) {
        this.armorContents[1] = itemStack;
        this.sendArmor( 1 );
    }

    public void setLeggings( ItemStack itemStack ) {
        this.armorContents[2] = itemStack;
        this.sendArmor( 2 );
    }

    public void setBoots( ItemStack itemStack ) {
        this.armorContents[3] = itemStack;
        this.sendArmor( 3 );
    }

    protected abstract void sendArmor( int slot );

}
