package io.gomint.server.inventory;

import io.gomint.inventory.ItemStack;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.network.PlayerConnection;
import io.gomint.server.network.packet.PacketContainerSetContent;
import io.gomint.server.network.packet.PacketInventoryContent;
import io.gomint.server.network.packet.PacketInventorySetSlot;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PlayerInventory extends MobInventory {

    private static final byte HOTBAR_SIZE = 9;
    private static final byte INV_SIZE = 36;

    private int[] hotbar = new int[HOTBAR_SIZE];
    private int itemInHandSlot;

    public PlayerInventory( EntityPlayer player ) {
        super( player, HOTBAR_SIZE + INV_SIZE );

        for ( int i = 0; i < this.hotbar.length; i++ ) {
            this.hotbar[i] = i + HOTBAR_SIZE;
        }

        sendContents( player.getConnection() );
    }

    /**
     * Set the item into the inventory without sending a packet to the client
     *
     * @param index     The slot to set
     * @param itemStack The item which should be set into
     */
    public void setItemWithoutSending( int index, ItemStack itemStack ) {
        this.contents[index] = itemStack;
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
        super.sendContents( slot, playerConnection );

        PacketInventorySetSlot setSlot = new PacketInventorySetSlot();
        setSlot.setSlot( slot );
        setSlot.setWindowId( (byte) 0 );
        setSlot.setItemStack( this.contents[slot] );
        playerConnection.addToSendQueue( setSlot );
    }

    /**
     * Send the whole inventory to the client, overwriting its current view
     *
     * @param playerConnection The connection to send this inventory to
     */
    public void sendContents( PlayerConnection playerConnection ) {
        super.sendContents( playerConnection );

        PacketInventoryContent inventory = new PacketInventoryContent();
        inventory.setWindowId( (byte) 0 );
        inventory.setItems( getContents() );
        playerConnection.send( inventory );
    }

}
