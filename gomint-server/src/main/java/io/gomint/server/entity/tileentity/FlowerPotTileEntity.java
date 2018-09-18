/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity.tileentity;

import io.gomint.entity.Entity;
import io.gomint.math.Location;
import io.gomint.math.Vector;
import io.gomint.server.inventory.InventoryHolder;
import io.gomint.server.inventory.item.Items;
import io.gomint.server.util.BlockIdentifier;
import io.gomint.server.world.WorldAdapter;
import io.gomint.taglib.NBTTagCompound;
import io.gomint.world.block.BlockFace;
import lombok.Getter;

/**
 * @author geNAZt
 * @version 1.0
 */
@Getter
public class FlowerPotTileEntity extends TileEntity implements InventoryHolder {

    private BlockIdentifier holding;

    /**
     * Create a new flower pot with the item given as content
     *
     * @param holding  which should be inside the flower pot
     * @param position of the flower pot
     */
    public FlowerPotTileEntity( BlockIdentifier holding, Location position ) {
        super( position );
        this.holding = holding;
    }

    /**
     * Construct new TileEntity from TagCompound
     *
     * @param tagCompound The TagCompound which should be used to read data from
     * @param world       The world in which this TileEntity resides
     * @param items       which should be used for generating item stacks
     */
    public FlowerPotTileEntity( NBTTagCompound tagCompound, WorldAdapter world, Items items ) {
        super( tagCompound, world, items );

        this.holding = getBlockIdentifier( tagCompound.getCompound( "PlantBlock", false ) );
    }

    @Override
    public void update( long currentMillis ) {

    }

    @Override
    public void interact( Entity entity, BlockFace face, Vector facePos, io.gomint.inventory.item.ItemStack item ) {

    }

    @Override
    public void toCompound( NBTTagCompound compound, SerializationReason reason ) {
        super.toCompound( compound, reason );

        compound.addValue( "id", "FlowerPot" );

        if ( this.holding != null ) {
            NBTTagCompound block = compound.getCompound( "PlantBlock", true );
            putBlockIdentifier( this.holding, block );
        }
    }

}
