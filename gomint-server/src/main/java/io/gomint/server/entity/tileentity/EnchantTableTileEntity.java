package io.gomint.server.entity.tileentity;

import io.gomint.server.world.WorldAdapter;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 */
public class EnchantTableTileEntity extends ContainerTileEntity {

    /**
     * Construct new TileEntity from TagCompound
     *
     * @param tagCompound The TagCompound which should be used to read data from
     * @param world       The world in which this TileEntity resides
     */
    public EnchantTableTileEntity( NBTTagCompound tagCompound, WorldAdapter world ) {
        super( tagCompound, world );
    }

    @Override
    public void toCompound( NBTTagCompound compound ) {
        super.toCompound( compound );

        compound.addValue( "id", "EnchantTable" );
    }
}
