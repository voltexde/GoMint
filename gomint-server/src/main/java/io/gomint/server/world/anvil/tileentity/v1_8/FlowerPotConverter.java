/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.anvil.tileentity.v1_8;

import io.gomint.math.Location;
import io.gomint.server.entity.tileentity.FlowerPotTileEntity;
import io.gomint.server.inventory.MaterialMagicNumbers;
import io.gomint.server.inventory.item.Items;
import io.gomint.server.world.WorldAdapter;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
public class FlowerPotConverter extends BasisConverter<FlowerPotTileEntity> {

    /**
     * Construct new converter
     *
     * @param worldAdapter for which we construct
     */
    public FlowerPotConverter( WorldAdapter worldAdapter ) {
        super( worldAdapter );
    }

    @Override
    public FlowerPotTileEntity readFrom( NBTTagCompound compound ) {
        // Read position
        Location position = getPosition( compound );

        // Read the item inside the pot
        int material = MaterialMagicNumbers.valueOfWithId( compound.getString( "Item", "minecraft:air" ) );

        // Skip non existent items for PE
        if ( material == 0 ) {
            return new FlowerPotTileEntity( Items.create( 0, (short) 0, (byte) 0, null ), position );
        }

        short data = compound.getInteger( "Data", -1 ).shortValue();
        return new FlowerPotTileEntity( Items.create( material, data, (byte) 1, null ), position );
    }

    @Override
    public void writeTo( FlowerPotTileEntity entity, NBTTagCompound compound ) {
        // Write basic stuff
        compound.addValue( "id", "FlowerPot" );
        writePosition( entity.getLocation(), compound );

        // Store the item
        compound.addValue( "Item", MaterialMagicNumbers.newIdFromValue( entity.getHoldingItem().getMaterial() ) );
        compound.addValue( "Data", entity.getHoldingItem().getData() );
    }

}
