/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity.tileentity;

import io.gomint.GoMint;
import io.gomint.entity.Entity;
import io.gomint.entity.EntityPlayer;
import io.gomint.inventory.item.ItemAir;
import io.gomint.math.Vector;
import io.gomint.server.GoMintServer;
import io.gomint.server.crafting.SmeltingRecipe;
import io.gomint.server.inventory.FurnaceInventory;
import io.gomint.server.inventory.InventoryHolder;
import io.gomint.server.inventory.item.ItemStack;
import io.gomint.server.network.packet.PacketSetContainerData;
import io.gomint.server.util.Pair;
import io.gomint.server.world.WorldAdapter;
import io.gomint.taglib.NBTTagCompound;
import io.gomint.world.block.BlockFace;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author geNAZt
 * @version 1.0
 */
public class FurnaceTileEntity extends ContainerTileEntity implements InventoryHolder {

    private static final int CONTAINER_PROPERTY_TICK_COUNT = 0;
    private static final int CONTAINER_PROPERTY_LIT_TIME = 1;

    private FurnaceInventory inventory;

    private short cookTime;
    private short burnTime;
    private short burnDuration;

    /**
     * Construct new TileEntity from TagCompound
     *
     * @param tagCompound The TagCompound which should be used to read data from
     * @param world       The world in which this TileEntity resides
     */
    public FurnaceTileEntity( NBTTagCompound tagCompound, WorldAdapter world ) {
        super( tagCompound, world );

        this.inventory = new FurnaceInventory( this );
        this.inventory.addObserver( new Consumer<Pair<Integer, io.gomint.inventory.item.ItemStack>>() {
            @Override
            public void accept( Pair<Integer, io.gomint.inventory.item.ItemStack> pair ) {
                if ( pair.getFirst() == 0 ) {
                    // Input slot has changed
                    onInputChanged( pair.getSecond() );
                }
            }
        } );

        List<Object> itemCompounds = tagCompound.getList( "Items", false );
        if ( itemCompounds != null ) {
            for ( Object itemCompound : itemCompounds ) {
                NBTTagCompound cd = (NBTTagCompound) itemCompound;

                byte slot = cd.getByte( "Slot", (byte) -1 );
                if ( slot == -1 ) {
                    this.inventory.addItem( getItemStack( cd ) );
                } else {
                    this.inventory.setItem( slot, getItemStack( cd ) );
                }
            }
        }

        this.cookTime = tagCompound.getShort( "CookTime", (short) 0 );
        this.burnTime = tagCompound.getShort( "BurnTime", (short) 0 );
        this.burnDuration = tagCompound.getShort( "BurnDuration", (short) 0 );
    }

    private void onInputChanged( io.gomint.inventory.item.ItemStack input ) {
        // If we currently smelt reset progress


        // Check if there is a smelting recipe present
        GoMintServer server = (GoMintServer) GoMint.instance();
        SmeltingRecipe recipe = server.getRecipeManager().getSmeltingRecipe( input );
        if ( recipe != null ) {
            for ( io.gomint.inventory.item.ItemStack stack : recipe.createResult() ) {
                System.out.println( stack );
            }
        }
    }

    @Override
    public void update( long currentMillis, float dF ) {

    }

    @Override
    public void interact( Entity entity, BlockFace face, Vector facePos, io.gomint.inventory.item.ItemStack item ) {
        if ( entity instanceof EntityPlayer ) {
            ( (EntityPlayer) entity ).openInventory( this.inventory );

            // Send the needed container data
            this.sendDataProperties( (io.gomint.server.entity.EntityPlayer) entity );
        }
    }

    public void sendDataProperties( io.gomint.server.entity.EntityPlayer player ) {
        PacketSetContainerData containerData = new PacketSetContainerData();
        containerData.setKey( CONTAINER_PROPERTY_TICK_COUNT );
        containerData.setValue( 120 );
        player.getConnection().addToSendQueue( containerData );

        containerData = new PacketSetContainerData();
        containerData.setKey( CONTAINER_PROPERTY_LIT_TIME );
        containerData.setValue( 120 );
        player.getConnection().addToSendQueue( containerData );

        /*containerData = new PacketSetContainerData();
        containerData.setKey( 2 );
        containerData.setValue( 200 );
        player.getConnection().addToSendQueue( containerData );*/
    }

    @Override
    public void toCompound( NBTTagCompound compound, SerializationReason reason ) {
        super.toCompound( compound, reason );

        compound.addValue( "id", "Furnace" );

        if ( reason == SerializationReason.PERSIST ) {
            List<NBTTagCompound> itemCompounds = new ArrayList<>();
            for ( int i = 0; i < this.inventory.size(); i++ ) {
                ItemStack itemStack = (ItemStack) this.inventory.getItem( i );
                if ( !( itemStack instanceof ItemAir ) ) {
                    NBTTagCompound itemCompound = new NBTTagCompound( "" );
                    itemCompound.addValue( "Slot", (byte) i );
                    putItemStack( itemStack, itemCompound );
                    itemCompounds.add( itemCompound );
                }
            }

            compound.addValue( "Items", itemCompounds );

            compound.addValue( "CookTime", (short) 120 /* this.cookTime */ );
            compound.addValue( "BurnTime", (short) 120 );
            compound.addValue( "BurnDuration", (short) 240 /* this.burnDuration */ );
        }
    }

}
