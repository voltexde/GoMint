package io.gomint.server.inventory;

import io.gomint.inventory.ItemStack;
import io.gomint.server.network.PlayerConnection;
import io.gomint.server.network.packet.PacketMobArmorEquipment;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author geNAZt
 * @version 1.0
 *          <p>
 *          Inventory for any type of mob. This holds additional Armor Contents
 */
public class MobInventory extends Inventory {

    @Getter
    protected int originalSize;

    public MobInventory( InventoryHolder inventoryHolder, int size ) {
        super( inventoryHolder, size + 4 );
        this.originalSize = size;
    }

    public void setHelmet( ItemStack itemStack ) {
        this.setItem( originalSize, itemStack );
    }

    public void setChestplate( ItemStack itemStack ) {
        this.setItem( originalSize + 1, itemStack );
    }

    public void setLeggings( ItemStack itemStack ) {
        this.setItem( originalSize + 2, itemStack );
    }

    public void setBoots( ItemStack itemStack ) {
        this.setItem( originalSize + 3, itemStack );
    }

    public ItemStack[] getContents() {
        return Arrays.copyOf( this.contents, this.originalSize );
    }

    @Override
    public int getSize() {
        return this.originalSize;
    }

    public void setItem( int index, ItemStack item ) {
        this.contents[index] = item;

        if ( index < this.originalSize ) {
            for ( PlayerConnection playerConnection : this.viewer ) {
                this.sendContents( index, playerConnection );
            }
        }
    }

    @Override
    public void sendContents( PlayerConnection playerConnection ) {
        PacketMobArmorEquipment armorEquipment = new PacketMobArmorEquipment();
        armorEquipment.setHelmet( this.contents[originalSize] );
        armorEquipment.setChestplate( this.contents[originalSize + 1] );
        armorEquipment.setLeggings( this.contents[originalSize + 2] );
        armorEquipment.setBoots( this.contents[originalSize + 3] );
        playerConnection.addToSendQueue( armorEquipment );
    }

    @Override
    public void sendContents( int slot, PlayerConnection playerConnection ) {
        if ( slot > originalSize ) {
            PacketMobArmorEquipment armorEquipment = new PacketMobArmorEquipment();
            armorEquipment.setHelmet( this.contents[originalSize] );
            armorEquipment.setChestplate( this.contents[originalSize + 1] );
            armorEquipment.setLeggings( this.contents[originalSize + 2] );
            armorEquipment.setBoots( this.contents[originalSize + 3] );
            playerConnection.addToSendQueue( armorEquipment );
        }
    }

}
