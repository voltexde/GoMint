package io.gomint.server.inventory;

import io.gomint.inventory.ItemStack;
import io.gomint.inventory.Material;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.network.PlayerConnection;
import io.gomint.server.network.packet.PacketContainerSetSlot;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author geNAZt
 * @version 1.0
 */
public abstract class Inventory {

    private final InventoryHolder owner;
    protected Set<PlayerConnection> viewer = new HashSet<>();

    protected int size;
    protected ItemStack[] contents;

    public Inventory( InventoryHolder owner, int size ) {
        this.owner = owner;
        this.size = size;

        this.contents = new ItemStack[size];
        Arrays.fill( this.contents, new ItemStack( Material.AIR, (short) 0, 0 ) );

        // Add owner to viewers if needed
        if ( this.owner instanceof EntityPlayer ) {
            addViewer( (EntityPlayer) this.owner );
        }
    }

    public void addViewer( EntityPlayer player ) {
        this.sendContents( player.getConnection() );
        this.viewer.add( player.getConnection() );
    }

    public void removeViewer( EntityPlayer player ) {
        this.viewer.remove( player.getConnection() );
    }

    public void setItem( int index, ItemStack item ) {
        this.contents[index] = item;

        for ( PlayerConnection playerConnection : this.viewer ) {
            this.sendContents( index, playerConnection );
        }
    }

    public ItemStack[] getContents() {
        return this.contents;
    }

    public int getSize() {
        return size;
    }

    public ItemStack getItem( int slot ) {
        return this.contents[slot];
    }

    public abstract void sendContents( PlayerConnection playerConnection );

    public abstract void sendContents( int slot, PlayerConnection playerConnection );

}
