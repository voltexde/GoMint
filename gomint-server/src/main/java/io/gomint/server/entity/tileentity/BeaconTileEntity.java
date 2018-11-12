/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity.tileentity;

import io.gomint.server.world.block.Block;
import io.gomint.taglib.NBTTagCompound;
import lombok.Data;

/**
 * @author geNAZt
 * @version 1.0
 */
@Data
public class BeaconTileEntity extends TileEntity {

    private int primary;
    private int secondary;

    /**
     * New beacon
     *
     * @param block which created this tile
     */
    public BeaconTileEntity( Block block ) {
        super( block );
    }

    @Override
    public void fromCompound( NBTTagCompound compound ) {
        super.fromCompound( compound );

        this.primary = compound.getInteger( "primary", 0 );
        this.secondary = compound.getInteger( "secondary", 0 );
    }

    @Override
    public void update( long currentMillis ) {

    }

    @Override
    public void toCompound( NBTTagCompound compound, SerializationReason reason ) {
        super.toCompound( compound, reason );

        compound.addValue( "id", "Beacon" );
        compound.addValue( "primary", this.primary );
        compound.addValue( "secondary", this.secondary );
    }

}
