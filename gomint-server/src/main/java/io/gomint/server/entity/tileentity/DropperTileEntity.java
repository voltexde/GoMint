/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity.tileentity;

import io.gomint.math.Location;
import io.gomint.server.inventory.DropperInventory;
import io.gomint.server.inventory.InventoryHolder;
import io.gomint.server.inventory.item.ItemAir;
import io.gomint.server.inventory.item.ItemStack;
import io.gomint.server.inventory.item.Items;
import io.gomint.server.world.WorldAdapter;
import io.gomint.taglib.NBTTagCompound;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author geNAZt
 * @version 1.0
 */
@Getter
public class DropperTileEntity extends TileEntity implements InventoryHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger( DropperTileEntity.class );
    private final DropperInventory inventory;

    /**
     * Construct a new dispenser
     *
     * @param items    which are inside the dispenser
     * @param location of the dispenser
     */
    public DropperTileEntity( ItemStack[] items, Location location ) {
        super( location );
        this.inventory = new DropperInventory( this );

        if ( items != null ) {
            for ( int i = 0; i < items.length; i++ ) {
                ItemStack itemStack = items[i];
                if ( itemStack != null ) {
                    this.inventory.setItem( i, itemStack );
                }
            }
        }
    }

    /**
     * Create a new tile entity based on its stored compound
     *
     * @param compound     which holds stored data
     * @param worldAdapter which holds this tile entity
     * @param items        which can be used to generate item stacks
     */
    public DropperTileEntity( NBTTagCompound compound, WorldAdapter worldAdapter, Items items ) {
        super( compound, worldAdapter, items );
        this.inventory = new DropperInventory( this );

        // Read in items
        List<Object> itemList = compound.getList( "Items", false );
        if ( itemList == null ) return;

        for ( Object item : itemList ) {
            NBTTagCompound itemCompound = (NBTTagCompound) item;

            ItemStack itemStack = getItemStack( itemCompound );
            if ( itemStack instanceof ItemAir ) {
                continue;
            }

            byte slot = itemCompound.getByte( "Slot", (byte) 127 );
            if ( slot == 127 ) {
                LOGGER.warn( "Found item without slot information: {} @ {} setting it to the next free slot", itemStack.getMaterial(), this.location );
                this.inventory.addItem( itemStack );
            } else {
                this.inventory.setItem( slot, itemStack );
            }
        }
    }

    @Override
    public void update( long currentMillis ) {

    }

    @Override
    public void toCompound( NBTTagCompound compound, SerializationReason reason ) {
        super.toCompound( compound, reason );

        compound.addValue( "id", "Dropper" );

        if ( reason == SerializationReason.PERSIST ) {
            List<NBTTagCompound> nbtTagCompounds = new ArrayList<>();
            for ( int i = 0; i < this.inventory.size(); i++ ) {
                ItemStack itemStack = (ItemStack) this.inventory.getItem( i );
                if ( itemStack != null ) {
                    NBTTagCompound nbtTagCompound = new NBTTagCompound( "" );
                    nbtTagCompound.addValue( "Slot", (byte) i );
                    putItemStack( itemStack, nbtTagCompound );
                    nbtTagCompounds.add( nbtTagCompound );
                }
            }

            compound.addValue( "Items", nbtTagCompounds );
        }
    }

}
