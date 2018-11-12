package io.gomint.server.entity.tileentity;

import io.gomint.server.world.block.Block;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
public class DaylightDetectorTileEntity extends TileEntity {

    /**
     * Construct new tile entity from position and world data
     *
     * @param block which created this tile
     */
    public DaylightDetectorTileEntity( Block block ) {
        super( block );
    }

    @Override
    public void update( long currentMillis ) {

    }

    @Override
    public void toCompound( NBTTagCompound compound, SerializationReason reason ) {
        super.toCompound( compound, reason );

        compound.addValue( "id", "DaylightDetector" );
    }

}
