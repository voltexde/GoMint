package io.gomint.server.inventory;

import io.gomint.entity.Entity;
import io.gomint.inventory.item.ItemStack;
import io.gomint.server.network.PlayerConnection;
import io.gomint.server.network.packet.PacketInventoryContent;
import io.gomint.server.network.packet.PacketInventorySetSlot;
import io.gomint.server.network.packet.PacketMobArmorEquipment;

/**
 * @author geNAZt
 * @version 1.0
 */
public class ArmorInventory extends Inventory {

    /**
     * Construct a inventory for holding armor items
     *
     * @param owner of this inventory
     */
    public ArmorInventory( InventoryHolder owner ) {
        super( owner, 4 );
    }

    /**
     * Set new helmet
     *
     * @param itemStack which will replace the old helmet
     */
    public void setHelmet( ItemStack itemStack ) {
        this.setItem( 0, itemStack );
    }

    /**
     * Set new chestplate
     *
     * @param itemStack which will replace the old chestplate
     */
    public void setChestplate( ItemStack itemStack ) {
        this.setItem( 1, itemStack );
    }

    /**
     * Set new leggings
     *
     * @param itemStack which will replace the old leggings
     */
    public void setLeggings( ItemStack itemStack ) {
        this.setItem( 2, itemStack );
    }

    /**
     * Set new boots
     *
     * @param itemStack which will replace the old boots
     */
    public void setBoots( ItemStack itemStack ) {
        this.setItem( 3, itemStack );
    }

    @Override
    public void sendContents( PlayerConnection playerConnection ) {
        if ( playerConnection.getEntity().equals( this.owner ) ) {
            PacketInventoryContent inventory = new PacketInventoryContent();
            inventory.setWindowId( WindowMagicNumbers.ARMOR.getId() );
            inventory.setItems( getContents() );
            playerConnection.send( inventory );
        } else {
            this.sendMobArmor( playerConnection );
        }
    }

    @Override
    public void sendContents( int slot, PlayerConnection playerConnection ) {
        if ( playerConnection.getEntity().equals( this.owner ) ) {
            PacketInventorySetSlot setSlot = new PacketInventorySetSlot();
            setSlot.setSlot( slot );
            setSlot.setWindowId( WindowMagicNumbers.ARMOR.getId() );
            setSlot.setItemStack( this.contents[slot] );
            playerConnection.addToSendQueue( setSlot );
        } else {
            this.sendMobArmor( playerConnection );
        }
    }

    private void sendMobArmor( PlayerConnection playerConnection ) {
        PacketMobArmorEquipment mobArmorEquipment = new PacketMobArmorEquipment();
        mobArmorEquipment.setBoots( this.contents[3] );
        mobArmorEquipment.setLeggings( this.contents[2] );
        mobArmorEquipment.setChestplate( this.contents[1] );
        mobArmorEquipment.setHelmet( this.contents[3] );
        mobArmorEquipment.setEntityId( ( (Entity) this.owner ).getEntityId() );
        playerConnection.addToSendQueue( mobArmorEquipment );
    }

}
