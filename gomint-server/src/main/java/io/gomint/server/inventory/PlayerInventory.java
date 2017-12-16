package io.gomint.server.inventory;

import com.koloboke.collect.ObjCursor;
import io.gomint.inventory.item.ItemStack;
import io.gomint.server.entity.Entity;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.entity.passive.EntityHuman;
import io.gomint.server.network.PlayerConnection;
import io.gomint.server.network.packet.PacketInventoryContent;
import io.gomint.server.network.packet.PacketInventorySetSlot;
import io.gomint.server.network.packet.PacketMobEquipment;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PlayerInventory extends Inventory implements io.gomint.inventory.PlayerInventory {

    private byte itemInHandSlot;

    /**
     * Construct a new Inventory for the player which is 36 + 9 in size
     *
     * @param player for which this inventory is
     */
    public PlayerInventory( EntityHuman player ) {
        super( player, 36 );
    }

    /**
     * Get the item this player is currently holding
     *
     * @return the itemstack the player is holding
     */
    public ItemStack getItemInHand() {
        return this.contents[this.itemInHandSlot];
    }

    @Override
    public void sendContents( int slot, PlayerConnection playerConnection ) {
        PacketInventorySetSlot setSlot = new PacketInventorySetSlot();
        setSlot.setSlot( slot );
        setSlot.setWindowId( WindowMagicNumbers.PLAYER.getId() );
        setSlot.setItemStack( this.contents[slot] );
        playerConnection.addToSendQueue( setSlot );
    }

    @Override
    public void sendContents( PlayerConnection playerConnection ) {
        PacketInventoryContent inventory = new PacketInventoryContent();
        inventory.setWindowId( WindowMagicNumbers.PLAYER.getId() );
        inventory.setItems( getContents() );
        playerConnection.addToSendQueue( inventory );
    }

    /**
     * Set the slot for the item the player currently has in hand
     *
     * @param slot in the inventory to point on the item in hand
     */
    public void setItemInHand( byte slot ) {
        this.itemInHandSlot = slot;
    }

    /**
     * Get the number of the slot the user is currently holding in hand
     *
     * @return the slot number for the in hand item
     */
    public byte getItemInHandSlot() {
        return this.itemInHandSlot;
    }

    @Override
    public void setItemInHandSlot( byte slot ) {
        if ( slot > 8 || slot < 0 ) {
            return;
        }

        this.itemInHandSlot = slot;

        PacketMobEquipment packetMobEquipment = new PacketMobEquipment();
        packetMobEquipment.setEntityId( ( (EntityPlayer) this.owner ).getEntityId() );
        packetMobEquipment.setSelectedSlot( slot );
        packetMobEquipment.setWindowId( (byte) 0 );
        packetMobEquipment.setSlot( (byte) ( slot + 9 ) );
        packetMobEquipment.setStack( this.getItemInHand() );

        // Relay packet
        ObjCursor<io.gomint.entity.Entity> entityObjCursor = ( (EntityPlayer) this.owner ).getAttachedEntities().cursor();
        while ( entityObjCursor.moveNext() ) {
            io.gomint.entity.Entity entity = entityObjCursor.elem();
            if ( entity instanceof EntityPlayer ) {
                ( (EntityPlayer) entity ).getConnection().addToSendQueue( packetMobEquipment );
            }
        }
    }

}
